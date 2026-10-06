package com.example.guardian_plus_mobile_app.features.healthmonitoring.application

import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.VitalSignRepository
import javax.inject.Inject


class GetLiveVitalSignsUseCase @Inject constructor(
    private val repository: VitalSignRepository
)  {
    suspend operator fun invoke(careRecipientProfileId: String): Result<LiveVitalSigns> =
    repository.getLiveVitalSigns(careRecipientProfileId)
}
