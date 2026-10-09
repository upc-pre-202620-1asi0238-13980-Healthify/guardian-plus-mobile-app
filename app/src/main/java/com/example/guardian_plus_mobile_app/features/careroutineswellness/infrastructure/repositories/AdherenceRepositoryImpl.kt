package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.AdherenceRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services.AdherenceService
import java.time.LocalDate
import javax.inject.Inject

/** First routine read from the platform; the rest still come from the simulated repositories. */
class AdherenceRepositoryImpl @Inject constructor(
    private val service: AdherenceService
) : AdherenceRepository {

    override suspend fun getAdherence(
        personUnderCareId: String,
        type: ReminderType,
        from: LocalDate,
        to: LocalDate
    ): Result<Adherence> = apiCall({ service.getAdherence(personUnderCareId, type.name, from.toString(), to.toString()) }) { dto ->
        Adherence(
            type = ReminderType.valueOf(dto.type),
            from = LocalDate.parse(dto.from),
            to = LocalDate.parse(dto.to),
            due = dto.due,
            confirmed = dto.confirmed,
            percentage = dto.percentage
        )
    }
}
