package com.example.guardian_plus_mobile_app.features.emergencyalerting.application

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSetting
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertChannelSettingRepository
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.NotificationChannel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.PushTokenProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RegisterPushDeviceUseCaseTest {

    /** Hands out [token], or fails like Firebase without Play Services. */
    private class FakePushTokenProvider(private val token: Result<String>) : PushTokenProvider {
        var deleted = false

        override suspend fun currentToken() = token

        override suspend fun deleteToken(): Result<Unit> {
            deleted = true
            return Result.success(Unit)
        }
    }

    /** Records the PUSH registrations it receives. */
    private class FakeAlertChannelSettingRepository : AlertChannelSettingRepository {
        val registrations = mutableListOf<Pair<String, String>>()

        override suspend fun registerPushDevice(userId: String, deviceToken: String): Result<AlertChannelSetting> {
            registrations += userId to deviceToken
            return Result.success(AlertChannelSetting(userId, NotificationChannel.PUSH, enabled = true, deviceTokenRegistered = true))
        }

        override suspend fun getChannelSettings(userId: String): Result<List<AlertChannelSetting>> = error("unused")
        override suspend fun configureChannel(
            userId: String,
            channel: NotificationChannel,
            enabled: Boolean
        ): Result<AlertChannelSetting> = error("unused")
    }

    @Test
    fun `the phone's current token is registered for the member`() = runBlocking {
        val repository = FakeAlertChannelSettingRepository()

        val result = RegisterPushDeviceUseCase(FakePushTokenProvider(Result.success("fcm-token")), repository)("user-1")

        assertTrue(result.isSuccess)
        assertEquals(listOf("user-1" to "fcm-token"), repository.registrations)
    }

    @Test
    fun `a token the provider just rotated is registered as it is`() = runBlocking {
        val repository = FakeAlertChannelSettingRepository()

        RegisterPushDeviceUseCase(FakePushTokenProvider(Result.success("old-token")), repository)("user-1", "new-token")

        assertEquals(listOf("user-1" to "new-token"), repository.registrations)
    }

    @Test
    fun `nothing is registered when the phone has no token`() = runBlocking {
        val repository = FakeAlertChannelSettingRepository()

        val result = RegisterPushDeviceUseCase(
            FakePushTokenProvider(Result.failure(IllegalStateException("sin Play Services"))), repository
        )("user-1")

        assertTrue(result.isFailure)
        assertTrue(repository.registrations.isEmpty())
    }

    @Test
    fun `signing out invalidates the phone's token`() = runBlocking {
        val provider = FakePushTokenProvider(Result.success("fcm-token"))

        UnregisterPushDeviceUseCase(provider)()

        assertTrue(provider.deleted)
    }
}
