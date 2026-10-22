package com.example.guardian_plus_mobile_app.features.profile.presentation.start

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.Primary
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton

/** Green splash shown while the signed-in account's care context loads; on failure it offers a retry. */
@Composable
fun SessionStartScreen(
    modifier: Modifier = Modifier,
    viewModel: SessionStartViewModel = hiltViewModel(),
    onDestination: (SessionStartDestination) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState) {
        (uiState as? SessionStartUiState.Done)?.let { onDestination(it.next) }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Primary)
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(R.string.app_name), color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(32.dp))
        when (val state = uiState) {
            is SessionStartUiState.Failed -> {
                Text(
                    text = stringResource(R.string.session_start_error_title),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.message, color = Color.White.copy(alpha = 0.75f), textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(24.dp))
                AuthPrimaryButton(text = stringResource(R.string.session_start_retry), isLoading = false, onClick = viewModel::load)
                TextButton(onClick = viewModel::signOut) {
                    Text(text = stringResource(R.string.session_start_logout), color = Color.White)
                }
            }
            else -> {
                CircularProgressIndicator(color = Color.White)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = stringResource(R.string.session_start_loading), color = Color.White.copy(alpha = 0.75f))
            }
        }
    }
}
