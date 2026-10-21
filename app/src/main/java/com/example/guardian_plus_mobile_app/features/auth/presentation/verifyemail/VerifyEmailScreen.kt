package com.example.guardian_plus_mobile_app.features.auth.presentation.verifyemail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.BrandGreen
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthCodeField
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthStepLayout

/** Second step of the sign-up: confirms the email with the EMAIL_VERIFICATION code IAM sent. */
@Composable
fun VerifyEmailScreen(
    modifier: Modifier = Modifier,
    userAccountId: String,
    email: String,
    viewModel: VerifyEmailViewModel = hiltViewModel(),
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
        VerifyEmailContent(
            uiState = uiState,
            email = email,
            onBackClick = onBackClick,
            onCodeChange = viewModel::onCodeChange,
            onVerifyClick = { viewModel.verify(userAccountId) },
            onResendClick = { viewModel.resend(userAccountId, resentMessage) }
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun VerifyEmailContent(
    modifier: Modifier = Modifier,
    uiState: VerifyEmailUiState,
    email: String,
    onBackClick: () -> Unit,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit,
    onResendClick: () -> Unit
) {
    AuthStepLayout(
        modifier = modifier,
        title = stringResource(R.string.auth_verify_email_title),
        subtitle = stringResource(R.string.auth_verify_email_subtitle, email),
        step = stringResource(R.string.auth_verify_email_step),
        onBackClick = onBackClick
    ) {
        AuthCodeField(value = uiState.code, onValueChange = onCodeChange, onDone = onVerifyClick)
        AuthErrorMessage(message = uiState.errorMessage)
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_verify_email_submit),
            isLoading = uiState.isLoading,
            onClick = onVerifyClick
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(
            onClick = onResendClick,
            enabled = !uiState.isResending,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.auth_resend_code), color = BrandGreen, fontFamily = FontFamily.Monospace)
        }
    }
}

@Preview
@Composable
private fun VerifyEmailContentPreview() {
    GuardianTheme(dynamicColor = false) {
        VerifyEmailContent(
            uiState = VerifyEmailUiState(code = "1234"),
            email = "maria@correo.com",
            onBackClick = {},
            onCodeChange = {},
            onVerifyClick = {},
            onResendClick = {}
        )
    }
}
