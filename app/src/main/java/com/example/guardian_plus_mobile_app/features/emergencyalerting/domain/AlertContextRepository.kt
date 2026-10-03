package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

interface AlertContextRepository {
    suspend fun getAlertContext(alert: Alert): Result<AlertContext>
}
