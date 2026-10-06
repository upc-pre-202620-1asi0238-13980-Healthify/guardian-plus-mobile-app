package com.example.guardian_plus_mobile_app.features.healthmonitoring.infrastructure.remote.dto

 data class LiveVitalSignsDto(
     val careRecipientProfileId: String,
     val retrievedAt: String,
     val vitalSigns: List<LiveVitalSignDto>
 )
