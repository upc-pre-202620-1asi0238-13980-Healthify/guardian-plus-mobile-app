package com.example.guardian_plus_mobile_app.features.auth.presentation.otp

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.BrandGreen
import com.example.guardian_plus_mobile_app.core.designsystem.theme.FieldBorder
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextMuted
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextPrimary
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.OtpPurpose
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthLayout
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthSheetTitle
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.OtpCodeBoxes
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.maskEmail

/**
 * Prototype's OTP screen, used for both codes IAM emails: the one that verifies a new account's email and
 * the second factor of a sign-in.
 */
@Composable
fun OtpVerificationScreen(
    modifier: Modifier = Modifier,
    userAccountId: String,
    email: String,
    purpose: OtpPurpose,
    viewModel: OtpVerificationViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onVerified: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resentMessage = stringResource(R.string.auth_code_resent)

    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            onVerified()
            viewModel.onNavigationHandled()
        }
    }

    LaunchedEffect(uiState.actionMessage) {
        uiState.actionMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onActionMessageShown()
        }
    }

    Box(modifier = modifier) {
        OtpVerificationContent(
            uiState = uiState,
            maskedEmail = maskEmail(email),
            onBackClick = onBackClick,
            onCodeChange = viewModel::onCodeChange,
            onVerifyClick = { viewModel.verify(userAccountId, purpose) },
            onResendClick = { viewModel.resend(userAccountId, purpose, resentMessage) }
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun OtpVerificationContent(
    modifier: Modifier = Modifier,
    uiState: OtpVerificationUiState,
    maskedEmail: String,
    onBackClick: () -> Unit,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
) {
    AuthLayout(
        modifier = modifier,
        headerTitle = stringResource(R.string.auth_otp_header_title),
        headerSubtitle = stringResource(R.string.auth_otp_header_subtitle)
    ) {
        AuthSheetTitle(
            title = stringResource(R.string.auth_otp_title),
            subtitle = stringResource(R.string.auth_otp_subtitle),
            highlight = maskedEmail,
            onBackClick = onBackClick
        )
        Spacer(modifier = Modifier.height(28.dp))
        OtpCodeBoxes(value = uiState.code, onValueChange = onCodeChange, onDone = onVerifyClick)
        Spacer(modifier = Modifier.height(12.dp))
        DemoCodeHint()
        AuthErrorMessage(message = uiState.errorMessage)
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_otp_submit),
            isLoading = uiState.isLoading,
            enabled = uiState.isComplete,
            icon = painterResource(R.drawable.ic_shield_check),
            onClick = onVerifyClick
        )
        Spacer(modifier = Modifier.height(20.dp))
        ResendRow(uiState = uiState, onResendClick = onResendClick)
        Spacer(modifier = Modifier.height(28.dp))
        ExpiryNotice()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

// While the platform sends no emails, the simulated code is shown so the flow can be tried
@Composable
private fun DemoCodeHint() {
    Text(
        text = buildAnnotatedString {
            append(stringResource(R.string.auth_otp_demo_code))
            withStyle(SpanStyle(color = BrandGreen)) { append(DemoSession.VERIFICATION_CODE) }
        },
        color = TextMuted,
        fontSize = 12.sp,
        fontFamily = FontFamily.Monospace,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun ResendRow(uiState: OtpVerificationUiState, onResendClick: () -> Unit) {
    if (uiState.resendAvailableInSec > 0) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.auth_otp_resend_in))
                withStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)) {
                    append(uiState.resendCountdown)
                }
            },
            color = TextMuted,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    } else {
        Text(
            text = stringResource(R.string.auth_resend_code),
            color = if (uiState.canResend) BrandGreen else TextMuted,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = uiState.canResend, onClick = onResendClick)
                .padding(vertical = 4.dp)
        )
    }
}

@Composable
private fun ExpiryNotice() {
    val time = stringResource(R.string.auth_otp_expiry_time)
    val notice = stringResource(R.string.auth_otp_expiry, time)
    val timeStart = notice.indexOf(time)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, FieldBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_shield_check),
            contentDescription = null,
            tint = BrandGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = buildAnnotatedString {
                append(notice)
                if (timeStart >= 0) {
                    addStyle(SpanStyle(color = TextPrimary, fontWeight = FontWeight.SemiBold), timeStart, timeStart + time.length)
                }
            },
            color = TextMuted,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
}

@Preview
@Composable
private fun OtpVerificationContentPreview() {
    GuardianTheme(dynamicColor = false) {
        OtpVerificationContent(
            uiState = OtpVerificationUiState(code = "12", resendAvailableInSec = 16),
            maskedEmail = "ma••••@correo.com",
            onBackClick = {},
            onCodeChange = {},
            onVerifyClick = {},
            onResendClick = {}
        )
    }
}
