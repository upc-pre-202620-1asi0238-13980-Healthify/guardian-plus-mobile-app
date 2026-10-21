package com.example.guardian_plus_mobile_app.features.auth.presentation.passwordreset

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.BrandGreen
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthStepLayout
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthTextField

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onOpenResetPassword: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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
        onSendClick = viewModel::requestReset,
        onHaveCodeClick = onOpenResetPassword
    )
}

@Composable
private fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    uiState: ForgotPasswordUiState,
    onBackClick: () -> Unit,
    onEmailChange: (String) -> Unit,
    onSendClick: () -> Unit,
    onHaveCodeClick: () -> Unit
) {
    AuthStepLayout(
        modifier = modifier,
        title = stringResource(R.string.auth_forgot_password_title),
        subtitle = stringResource(R.string.auth_forgot_password_subtitle),
        onBackClick = onBackClick
    ) {
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
        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = onHaveCodeClick, modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(
                text = stringResource(R.string.auth_forgot_password_have_code),
                color = BrandGreen,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Preview
@Composable
private fun ForgotPasswordContentPreview() {
    GuardianTheme(dynamicColor = false) {
        ForgotPasswordContent(
            uiState = ForgotPasswordUiState(email = "maria@correo.com"),
            onBackClick = {},
            onEmailChange = {},
            onSendClick = {},
            onHaveCodeClick = {}
        )
    }
}
