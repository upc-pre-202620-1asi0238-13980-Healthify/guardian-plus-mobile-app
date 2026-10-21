package com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.services

import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.ConfirmMedicationAcquisitionRequestDto
import com.example.guardian_plus_mobile_app.features.careroutineswellness.infrastructure.remote.dto.MedicationStockDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Path

interface MedicationStockService {

    @GET("medication-stocks/citizen/{personUnderCareId}")
    suspend fun getMedicationStocks(@Path("personUnderCareId") personUnderCareId: String): Response<List<MedicationStockDto>>

    @PUT("medication-stocks/{medicationStockId}/acquisition")
    suspend fun confirmMedicationAcquisition(
        @Path("medicationStockId") medicationStockId: String,
        @Body request: ConfirmMedicationAcquisitionRequestDto
    ): Response<MedicationStockDto>
}
