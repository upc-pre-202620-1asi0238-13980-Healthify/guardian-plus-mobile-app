package com.example.guardian_plus_mobile_app.core.time

import java.time.Instant
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

/**
 * The backend's clock as seen from the phone. Escalation deadlines are decided on the server, so
 * countdowns must use its time: a phone clock that is 30 s off would show 1:30 left for a 60 s timeout.
 * The offset is learned from the Date header of every response (see NetworkModule).
 */
@Singleton
class ServerClock @Inject constructor() {

    @Volatile
    private var offsetMillis = 0L

    fun now(): Instant = Instant.now().plusMillis(offsetMillis)

    fun sync(serverDate: Date) {
        offsetMillis = serverDate.time - System.currentTimeMillis()
    }
}
