package com.example.guardian_plus_mobile_app.features.auth.presentation.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.BrandGreen
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextMuted
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthDivider
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthLayout
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPasswordField
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthSheetTitle
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthTermsText
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthTextField
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.GoogleButton

@Composable
fun RegisterScreen(
    modifier: Modifier = Modifier,
    viewModel: RegisterViewModel = hiltViewModel(),
    onLoginClick: () -> Unit,
    onRegistered: (userAccountId: String, email: String) -> Unit,
    onGoogleClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.registeredAccount) {
        uiState.registeredAccount?.let { account ->
            onRegistered(account.userAccountId, account.email)
            viewModel.onNavigationHandled()
        }
    }

    RegisterContent(
        modifier = modifier,
        uiState = uiState,
        onFullNameChange = viewModel::onFullNameChange,
        onEmailChange = viewModel::onEmailChange,
        onPasswordChange = viewModel::onPasswordChange,
        onConfirmPasswordChange = viewModel::onConfirmPasswordChange,
        onTogglePasswordVisibility = viewModel::togglePasswordVisibility,
        onRegisterClick = viewModel::register,
        onGoogleClick = onGoogleClick,
        onLoginClick = onLoginClick
    )
}

@Composable
private fun RegisterContent(
    modifier: Modifier = Modifier,
    uiState: RegisterUiState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onRegisterClick: () -> Unit,
    onGoogleClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    AuthLayout(
        modifier = modifier,
        headerTitle = stringResource(R.string.auth_register_header_title),
        headerSubtitle = stringResource(R.string.auth_register_header_subtitle)
    ) {
        AuthSheetTitle(
            title = stringResource(R.string.auth_register_title),
            subtitle = stringResource(R.string.auth_register_subtitle)
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthTextField(
            label = stringResource(R.string.auth_full_name_label),
            value = uiState.fullName,
            onValueChange = onFullNameChange,
            placeholder = stringResource(R.string.auth_full_name_placeholder)
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthTextField(
            label = stringResource(R.string.auth_email_label),
            value = uiState.email,
            onValueChange = onEmailChange,
            placeholder = stringResource(R.string.auth_email_placeholder),
            keyboardType = KeyboardType.Email
        )
        Spacer(modifier = Modifier.height(16.dp))
        AuthPasswordField(
            label = stringResource(R.string.auth_password_label),
            value = uiState.password,
            onValueChange = onPasswordChange,
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
            onImeAction = onRegisterClick
        )
        AuthErrorMessage(message = uiState.errorMessage)
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_register_submit),
            isLoading = uiState.isLoading,
            onClick = onRegisterClick
        )
        Spacer(modifier = Modifier.height(24.dp))
        AuthDivider(text = stringResource(R.string.auth_register_divider))
        Spacer(modifier = Modifier.height(24.dp))
        GoogleButton(text = stringResource(R.string.auth_register_google), onClick = onGoogleClick)
        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            Text(text = stringResource(R.string.auth_register_have_account), color = TextMuted, fontSize = 15.sp)
            Text(
                text = stringResource(R.string.auth_register_login),
                color = BrandGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.clickable(onClick = onLoginClick)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        AuthTermsText()
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Preview
@Composable
private fun RegisterContentPreview() {
    GuardianTheme(dynamicColor = false) {
        RegisterContent(
            uiState = RegisterUiState(fullName = "María Rodríguez"),
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onTogglePasswordVisibility = {},
            onRegisterClick = {},
            onGoogleClick = {},
            onLoginClick = {}
        )
    }
}
