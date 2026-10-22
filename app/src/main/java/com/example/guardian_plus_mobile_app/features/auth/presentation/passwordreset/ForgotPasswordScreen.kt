package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthLayout
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthSheetTitle
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthTextField
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.BackToLoginLink

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOpenResetPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // The email carries the token; the next screen is where the user types it with the new password
    LaunchedEffect(uiState.isSent) {
        if (uiState.isSent) {
            onOpenResetPassword()
            viewModel.onNavigationHandled()
        }
    }

    ForgotPasswordContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onEmailChange = viewModel::onEmailChange,
        onSendClick = viewModel::requestReset
    )
}

@Composable
private fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    uiState: ForgotPasswordUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    AuthLayout(
        modifier = modifier,
        headerTitle = stringResource(R.string.auth_forgot_header_title),
        headerSubtitle = stringResource(R.string.auth_forgot_header_subtitle)
    ) {
        AuthSheetTitle(
            title = stringResource(R.string.auth_forgot_password_title),
            subtitle = stringResource(R.string.auth_forgot_password_subtitle)
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(
            label = stringResource(R.string.auth_email_label),
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.auth_email_placeholder),
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done,
            onImeAction = onSendClick
        )
        AuthErrorMessage(message = uiState.errorMessage)
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_forgot_password_submit),
            isLoading = uiState.isLoading,
            onClick = onSendClick
        )
        Spacer(modifier = Modifier.height(16.dp))
        BackToLoginLink(modifier = Modifier.align(Alignment.CenterHorizontally), onClick = onBackClick)
    }
}

@Preview
@Composable
private fun ForgotPasswordContentPreview() {
    GuardianTheme(dynamicColor = false) {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(),
            onBackClick = {},
            onEmailChange = {},
            onSendClick = {}
        )
    }
}
