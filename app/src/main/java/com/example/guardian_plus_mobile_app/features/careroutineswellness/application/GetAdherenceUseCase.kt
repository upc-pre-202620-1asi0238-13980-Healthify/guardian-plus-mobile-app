package com.example.guardian_plus_mobile_app.features.careroutineswellness.application

import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.Adherence
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.AdherenceRepository
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.ReminderType
import java.time.LocalDate
import javax.inject.Inject

class GetAdherenceUseCase @Inject constructor(
    private val repository: AdherenceRepository
) {
    suspend operator fun invoke(
        personUnderCareId: String,
        type: ReminderType,
        from: LocalDate,
        to: LocalDate
    ): Result<Adherence> = repository.getAdherence(personUnderCareId, type, from, to)
}
