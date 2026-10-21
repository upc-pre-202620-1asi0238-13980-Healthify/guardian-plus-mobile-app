package com.example.guardian_plus_mobile_app.features.auth.presentation.twofactor

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthCodeField
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthStepLayout

/** OTP challenge of an account with two-factor enabled; the session is issued once the code matches. */
@Composable
fun TwoFactorScreen(
    modifier: Modifier = Modifier,
    userAccountId: String,
    viewModel: TwoFactorViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onAuthenticated: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthenticated()
            viewModel.onNavigationHandled()
        }
    }

    TwoFactorContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onCodeChange = viewModel::onCodeChange,
        onVerifyClick = { viewModel.verify(userAccountId) }
    )
}

@Composable
private fun TwoFactorContent(
    modifier: Modifier = Modifier,
    uiState: TwoFactorUiState,
    onBackClick: () -> Unit,
    onCodeChange: (String) -> Unit,
    onVerifyClick: () -> Unit
) {
    AuthStepLayout(
        modifier = modifier,
        title = stringResource(R.string.auth_two_factor_title),
        subtitle = stringResource(R.string.auth_two_factor_subtitle),
        onBackClick = onBackClick
    ) {
        AuthCodeField(value = uiState.code, onValueChange = onCodeChange, onDone = onVerifyClick)
        AuthErrorMessage(message = uiState.errorMessage)
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.auth_two_factor_submit),
            isLoading = uiState.isLoading,
            onClick = onVerifyClick
        )
    }
}

@Preview
@Composable
private fun TwoFactorContentPreview() {
    GuardianTheme(dynamicColor = false) {
        TwoFactorContent(
            uiState = TwoFactorUiState(code = "12", errorMessage = "El código no es válido o ya venció"),
            onBackClick = {},
            onCodeChange = {},
            onVerifyClick = {}
        )
    }
}
