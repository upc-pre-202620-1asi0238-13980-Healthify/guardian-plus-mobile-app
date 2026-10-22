package com.example.guardian_plus_mobile_app.features.auth.presentation.otp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class OtpVerificationUiStateTest {

    @Test
    fun `the code is complete with its six digits`() {
        assertFalse(OtpVerificationUiState(code = "12345").isComplete)
        assertTrue(OtpVerificationUiState(code = "123456").isComplete)
    }

    @Test
    fun `a new code can only be asked for once the countdown ends`() {
        assertFalse(OtpVerificationUiState(resendAvailableInSec = 16).canResend)
        assertTrue(OtpVerificationUiState(resendAvailableInSec = 0).canResend)
        assertFalse(OtpVerificationUiState(resendAvailableInSec = 0, isResending = true).canResend)
    }

    @Test
    fun `the countdown reads as minutes and seconds`() {
        assertEquals("0:16", OtpVerificationUiState(resendAvailableInSec = 16).resendCountdown)
        assertEquals("1:05", OtpVerificationUiState(resendAvailableInSec = 65).resendCountdown)
    }
}
