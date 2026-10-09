package com.example.guardian_plus_mobile_app.features.careroutineswellness.domain

import java.time.LocalDate

interface AdherenceRepository {
    suspend fun getAdherence(personUnderCareId: String, type: ReminderType, from: LocalDate, to: LocalDate): Result<Adherence>
}
