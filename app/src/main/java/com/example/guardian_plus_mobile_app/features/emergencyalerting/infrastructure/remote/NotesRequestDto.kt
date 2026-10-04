package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

/** Body of stabilize, close and complete-response: optional free-text notes (up to 2000 characters). */
data class NotesRequestDto(
    val notes: String?
)
