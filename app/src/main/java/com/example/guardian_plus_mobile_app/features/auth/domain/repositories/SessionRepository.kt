package com.example.guardian_plus_mobile_app.features.auth.domain.repositories

import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession

/** Keeps the session on the device, so the token travels with every request and survives a restart. */
interface SessionRepository {
    fun currentSession(): AuthenticatedSession?

    fun save(session: AuthenticatedSession)

    fun clear()
}
