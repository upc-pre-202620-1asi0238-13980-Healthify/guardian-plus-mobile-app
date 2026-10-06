package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.VitalSignRepository
import java.time.LocalDate
import javax.inject.Inject

//this use case is for fetching the history, or readings in general not the live ones specifically


class GetVitalSignHistoryUseCase @Inject constructor(
    private val repository: VitalSignRepository
) {
    suspend operator fun invoke(
        careRecipientProfileId: String,
        from: LocalDate,
        to: LocalDate
    ) : Result<List<VitalSignReading>> = repository.getVitalSignHistory(careRecipientProfileId, from, to)
}
