package com.example.guardian_plus_mobile_app.features.emergencyalerting.domain

/** The push provider's address of this phone (Firebase Cloud Messaging), where the platform sends alerts. */
interface PushTokenProvider {
    suspend fun currentToken(): Result<String>

    /** Invalidates this phone's token, so alerts for the account that signed out stop arriving. */
    suspend fun deleteToken(): Result<Unit>
}
