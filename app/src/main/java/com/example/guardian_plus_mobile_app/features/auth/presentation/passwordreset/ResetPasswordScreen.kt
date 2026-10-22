package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
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
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPasswordField
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthSheetTitle
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthTextField

/** Sets the new password with the token of the recovery email, which IAM consumes on use. */
@Composable
fun ResetPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ResetPasswordViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPasswordReset: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isReset) {
        if (uiState.isReset) {
            onPasswordReset()
            viewModel.onNavigationHandled()
        }
    }

    ResetPasswordContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onTokenChange = viewModel::onTokenChange,
        onNewPasswordChange = viewModel::onNewPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onResetClick = viewModel::reset
    )
}

@Composable
private fun ResetPasswordContent(
    modifier: Modifier = Modifier,
    uiState: ResetPasswordUiState,
    onBackClick: () -> Unit,
    onTokenChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onResetClick: () -> Unit
) {
    AuthLayout(
        modifier = modifier,
        headerTitle = stringResource(R.string.auth_reset_header_title),
        headerSubtitle = stringResource(R.string.auth_reset_header_subtitle)
    ) {
        AuthSheetTitle(
            title = stringResource(R.string.auth_reset_password_title),
            subtitle = stringResource(R.string.auth_reset_password_subtitle),
            onBackClick = onBackClick
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(
            label = stringResource(R.string.auth_code_label),
            value = uiState.token,
            onValueChange = onTokenChange,
            placeholder = stringResource(R.string.auth_code_placeholder),
            keyboardType = KeyboardType.Number
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthPasswordField(
            label = stringResource(R.string.auth_password_label),
            value = uiState.newPassword,
            onValueChange = onNewPasswordChange,
            placeholder = stringResource(R.string.auth_password_placeholder),
            isHidden = uiState.isPasswordHidden,
            onToggleVisibility = onTogglePasswordVisibility
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthPasswordField(
            label = stringResource(R.string.auth_confirm_password_label),
            value = uiState.confirmPassword,
            onValueChange = onConfirmPasswordChange,
            placeholder = stringResource(R.string.auth_confirm_password_placeholder),
            isHidden = uiState.isPasswordHidden,
            onToggleVisibility = onTogglePasswordVisibility,
            imeAction = ImeAction.Done,
            onImeAction = onResetClick
        )
        AuthErrorMessage(message = uiState.errorMessage)
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_reset_password_submit),
            isLoading = uiState.isLoading,
            onClick = onResetClick
        )
    }
}

@Preview
@Composable
private fun ResetPasswordContentPreview() {
    GuardianTheme(dynamicColor = false) {
        ResetPasswordContent(
            uiState = ResetPasswordUiState(token = "123456"),
            onBackClick = {},
            onTokenChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onResetClick = {}
        )
    }
}
