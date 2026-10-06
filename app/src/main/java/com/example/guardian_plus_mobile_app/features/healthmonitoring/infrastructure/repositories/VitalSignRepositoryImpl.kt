package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.repositories

import com.example.guardian_plus_mobile_app.core.network.apiCall
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSign
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.LiveVitalSigns
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.ReadingClassification
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignReading
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.repositories.VitalSignRepository
import com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.services.VitalSignService
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject


class VitalSignRepositoryImpl @Inject constructor(
    private val service: VitalSignService
) : VitalSignRepository {


    override suspend fun getLiveVitalSigns(careRecipientProfileId: String): Result<LiveVitalSigns>  =
        apiCall({ service.getLiveVitalSigns(careRecipientProfileId)}) {
            dto ->
            LiveVitalSigns(
                careRecipientProfileId = dto.careRecipientProfileId,
                retrievedAt = Instant.parse(dto.retrievedAt),
                vitalSigns = dto.vitalSigns.map { item ->
                    LiveVitalSign(
                        id = item.vitalSignId,
                        type = VitalSignType.valueOf(item.vitalSignType),
                        typeName = item.vitalSignTypeName,
                        unit = item.unit,
                        value = item.value,
                        measuredAt = Instant.parse(item.measuredAt),
                        normalMinimum = item.normalMinimum,
                        normalMaximum = item.normalMaximum,
                        classification = ReadingClassification.valueOf(item.classification),
                        liveSignal = item.liveSignal
                    )
                }
            )
        }

    override suspend fun getVitalSignHistory(
        careRecipientProfileId: String,
        from: LocalDate,
        to: LocalDate
    ): Result<List<VitalSignReading>> = apiCall({service.getVitalSignHistory(careRecipientProfileId, from.toString(), to.toString())}){
        dtos -> dtos.map{
            dto ->
            VitalSignReading(
                id = dto.id,
                type = VitalSignType.valueOf(dto.vitalSignType),
                value = dto.value,
                measuredAt = Instant.parse(dto.measuredAt)
            )
        }

    }    
}
