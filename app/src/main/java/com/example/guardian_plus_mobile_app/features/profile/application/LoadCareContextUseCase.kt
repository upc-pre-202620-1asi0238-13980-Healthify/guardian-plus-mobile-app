package com.example.guardian_plus_mobile_app.features.profile.application

import com.example.guardian_plus_mobile_app.core.session.CareContext
import com.example.guardian_plus_mobile_app.core.session.CareContextStore
import com.example.guardian_plus_mobile_app.core.session.SignUpNameStore
import com.example.guardian_plus_mobile_app.features.auth.application.GetCurrentUserAccountUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.application.GetEmergencyContactsUseCase
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.profile.domain.repositories.ProfileRepository
import javax.inject.Inject

/**
 * Builds the [CareContext] of the signed-in account: who it is (IAM's /user-accounts/me), its profile and the
 * person under care it shows (Profile; see [chooseCareRelationship], since one account may care for several),
 * and the number to call (Emergency & Alerting's first emergency contact). The result is kept in
 * [CareContextStore] for every screen. [preferredCareRecipientProfileId] is the person just picked or added.
 */
class LoadCareContextUseCase @Inject constructor(
    private val getCurrentUserAccount: GetCurrentUserAccountUseCase,
    private val profileRepository: ProfileRepository,
    private val getEmergencyContacts: GetEmergencyContactsUseCase,
    private val careContextStore: CareContextStore,
    private val signUpNameStore: SignUpNameStore
) {
    suspend operator fun invoke(preferredCareRecipientProfileId: String? = null): Result<CareContextLoad> = runCatching {
        val account = getCurrentUserAccount().getOrThrow()
        val userProfile = profileRepository.findUserProfile(account.id).getOrThrow()
        // The person the device already showed is only kept for the same account
        val current = careContextStore.current()?.takeIf { it.userAccountId == account.id }
        val relationship = chooseCareRelationship(
            relationships = profileRepository.getCareRelationships(account.id).getOrThrow(),
            preferredCareRecipientProfileId = preferredCareRecipientProfileId,
            currentCareRecipientProfileId = current?.careRecipientProfileId
        )
        val careRecipient = relationship?.let { profileRepository.getCareRecipient(it.careRecipientProfileId).getOrThrow() }

        if (userProfile == null || careRecipient == null) {
            return@runCatching CareContextLoad.OnboardingRequired(
                userAccountId = account.id,
                needsUserProfile = userProfile == null,
                needsCareRecipient = careRecipient == null,
                suggestedFullName = signUpNameStore.find(account.id)
            )
        }
        val careContext = CareContext(
            userAccountId = account.id,
            userFullName = userProfile.fullName,
            careRecipientProfileId = careRecipient.id,
            careRecipientName = careRecipient.fullName,
            // Without contacts there is simply nobody to call yet
            callNumber = getEmergencyContacts(careRecipient.id).getOrNull()?.firstToCall()
        )
        careContextStore.save(careContext)
        signUpNameStore.remove(account.id)
        CareContextLoad.Ready(careContext)
    }
}

/** The phone of the active contact with the highest priority (1 is the primary contact). */
private fun List<EmergencyContact>.firstToCall(): String? =
    filter { it.active }.minByOrNull { it.priorityOrder }?.phoneNumber
