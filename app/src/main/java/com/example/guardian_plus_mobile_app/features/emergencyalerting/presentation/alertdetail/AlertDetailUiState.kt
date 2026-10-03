package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertContext
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertDelivery
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.DeliveryStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Incident
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.IncidentStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.ResponseStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.confirmationSecondsLeft
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.escalationSecondsLeft
import java.time.Instant

/** Commands the member can run from the detail; used to show progress on the right button. */
enum class AlertDetailAction {
    ACKNOWLEDGE,
    CLAIM,
    COMPLETE,
    STABILIZE,
    CLOSE
}

/** Where an emergency contact stands for this alert, from "not sent yet" to "arrived". */
enum class ContactStatus {
    WAITING,
    SENDING,
    SENT,
    DELIVERED,
    FAILED,
    ACKNOWLEDGED,
    ON_THE_WAY,
    ARRIVED,
    // Never reached because someone acknowledged before the escalation got to them
    NOT_NEEDED
}

data class EscalationContact(
    val contact: EmergencyContact,
    val status: ContactStatus
)

/** One line of "Seguimiento del incidente"; the screen turns each kind into its sentence. */
sealed interface TimelineEvent {
    val at: Instant

    data class Detected(override val at: Instant) : TimelineEvent
    data class Confirmed(override val at: Instant) : TimelineEvent
    data class Dismissed(override val at: Instant) : TimelineEvent
    data class SentTo(override val at: Instant, val names: List<String>) : TimelineEvent
    data class Acknowledged(override val at: Instant, val name: String?) : TimelineEvent
    data class OnTheWay(override val at: Instant, val name: String?) : TimelineEvent
    data class Arrived(override val at: Instant, val name: String?) : TimelineEvent
    data class Stabilized(override val at: Instant) : TimelineEvent
    data class Closed(override val at: Instant) : TimelineEvent
    // Not happened yet: when the alert will reach the next contacts if nobody acknowledges it
    data class NextEscalation(override val at: Instant) : TimelineEvent
}

data class AlertDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val alert: Alert? = null,
    val incident: Incident? = null,
    val context: AlertContext = AlertContext(),
    val contacts: List<EmergencyContact> = emptyList(),
    val ackTimeoutSec: Int = 60,
    val escalationEnabled: Boolean = true,
    val careRecipientFirstName: String = "",
    val currentUserId: String = "",
    val runningAction: AlertDetailAction? = null,
    val actionErrorMessage: String? = null,
    val now: Instant = Instant.now()
) {
    private val wasNotifiedToMe: Boolean
        get() = alert?.deliveries?.any { it.recipientUserId == currentUserId } == true

    private val incidentOpen: Boolean
        get() = incident == null || incident.status == IncidentStatus.IN_ATTENTION

    val isFinished: Boolean
        get() = alert?.status == AlertStatus.RESOLVED ||
            alert?.status == AlertStatus.DISMISSED ||
            incident?.status == IncidentStatus.CLOSED

    // The backend accepts these commands only from members the alert was sent to
    val canAcknowledge: Boolean
        get() = alert?.status?.isAwaitingAcknowledgement == true && wasNotifiedToMe

    // Once someone is on the way or already arrived, offering "Voy en camino" again would only confuse
    val canClaim: Boolean
        get() = alert != null && wasNotifiedToMe && incidentOpen &&
            alert.responses.none { it.status == ResponseStatus.CLAIMED || it.status == ResponseStatus.COMPLETED } &&
            (alert.status.isAwaitingAcknowledgement || alert.status == AlertStatus.ACKNOWLEDGED)

    /** "Llegué" is offered only to the member who said they were on the way. */
    val myActiveResponseId: String?
        get() = alert?.activeResponse?.takeIf { it.responderUserId == currentUserId }?.id

    val canStabilize: Boolean get() = incident?.status == IncidentStatus.IN_ATTENTION
    val canClose: Boolean get() = incident != null && incident.status != IncidentStatus.CLOSED

    val escalationSecondsLeft: Long?
        get() = alert?.escalationSecondsLeft(ackTimeoutSec, escalationEnabled, now)

    val confirmationSecondsLeft: Long?
        get() = alert?.confirmationSecondsLeft(now)

    fun contactName(userId: String?): String? =
        contacts.firstOrNull { it.userId == userId }?.displayName

    /** Every active contact in escalation order, with how far the alert got with each of them. */
    val escalationContacts: List<EscalationContact>
        get() {
            val alert = alert ?: return emptyList()
            return contacts
                .filter { it.active }
                .sortedBy { it.priorityOrder }
                .map { contact -> EscalationContact(contact, alert.statusOf(contact.userId)) }
        }

    private fun Alert.statusOf(userId: String): ContactStatus {
        val response = responses.lastOrNull { it.responderUserId == userId }
        return when {
            response?.status == ResponseStatus.COMPLETED -> ContactStatus.ARRIVED
            response?.status == ResponseStatus.CLAIMED -> ContactStatus.ON_THE_WAY
            acknowledgedByUserId == userId -> ContactStatus.ACKNOWLEDGED
            deliveries.none { it.recipientUserId == userId } && !status.isAwaitingAcknowledgement ->
                ContactStatus.NOT_NEEDED
            else -> bestDeliveryStatus(deliveries.filter { it.recipientUserId == userId })
        }
    }

    // A contact may get the alert through several channels (in-app and SMS); the best outcome counts
    private fun bestDeliveryStatus(deliveries: List<AlertDelivery>): ContactStatus = when {
        deliveries.isEmpty() -> ContactStatus.WAITING
        deliveries.any { it.status == DeliveryStatus.DELIVERED } -> ContactStatus.DELIVERED
        deliveries.any { it.status == DeliveryStatus.SENT } -> ContactStatus.SENT
        deliveries.any { it.status == DeliveryStatus.PENDING } -> ContactStatus.SENDING
        else -> ContactStatus.FAILED
    }

    /** What happened so far, oldest first, built only from the timestamps the backend recorded. */
    val timeline: List<TimelineEvent>
        get() {
            val alert = alert ?: return emptyList()
            val events = mutableListOf<TimelineEvent>(TimelineEvent.Detected(alert.triggeredAt))
            // Only falls have a confirmation window; other sources are confirmed the moment they arrive
            if (alert.sourceType == AlertSourceType.FALL_DETECTED) {
                alert.confirmedAt?.let { events += TimelineEvent.Confirmed(it) }
            }
            if (alert.status == AlertStatus.DISMISSED) {
                events += TimelineEvent.Dismissed(alert.resolvedAt ?: alert.triggeredAt)
            }
            // One line per dispatch wave (primary, secondary, everyone)
            alert.deliveries
                .filter { it.sentAt != null }
                .groupBy { it.recipientLevel }
                .forEach { (_, wave) ->
                    val names = wave.map { contactName(it.recipientUserId) ?: "" }.filter { it.isNotEmpty() }.distinct()
                    events += TimelineEvent.SentTo(wave.minOf { it.sentAt!! }, names)
                }
            alert.acknowledgedAt?.let { events += TimelineEvent.Acknowledged(it, contactName(alert.acknowledgedByUserId)) }
            alert.responses.forEach { response ->
                val name = contactName(response.responderUserId)
                events += TimelineEvent.OnTheWay(response.claimedAt, name)
                response.completedAt?.let { events += TimelineEvent.Arrived(it, name) }
            }
            incident?.stabilizedAt?.let { events += TimelineEvent.Stabilized(it) }
            incident?.closedAt?.let { events += TimelineEvent.Closed(it) }
            if (alert.currentRecipientLevel != RecipientLevel.BROADCAST) {
                escalationSecondsLeft?.let { left -> events += TimelineEvent.NextEscalation(now.plusSeconds(left)) }
            }
            return events.sortedBy { it.at }
        }
}
