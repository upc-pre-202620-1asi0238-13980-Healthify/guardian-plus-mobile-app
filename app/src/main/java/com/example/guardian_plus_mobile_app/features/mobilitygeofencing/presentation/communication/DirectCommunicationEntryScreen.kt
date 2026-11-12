package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.DirectCommunicationRoutingResult
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.application.InitiateDirectCommunicationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface DirectCommunicationEntryUiState {
    data object Evaluating : DirectCommunicationEntryUiState
    data class RouteReady(val routingResult: DirectCommunicationRoutingResult) : DirectCommunicationEntryUiState
    data class Error(val message: String) : DirectCommunicationEntryUiState
}

@HiltViewModel
class DirectCommunicationRouterViewModel @Inject constructor(
    private val initiateDirectCommunicationUseCase: InitiateDirectCommunicationUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<DirectCommunicationEntryUiState>(DirectCommunicationEntryUiState.Evaluating)
    val uiState: StateFlow<DirectCommunicationEntryUiState> = _uiState.asStateFlow()

    fun evaluateAndInitiate(fragileCitizenId: String) {
        viewModelScope.launch {
            _uiState.update { DirectCommunicationEntryUiState.Evaluating }
            initiateDirectCommunicationUseCase(fragileCitizenId)
                .onSuccess { result ->
                    _uiState.update { DirectCommunicationEntryUiState.RouteReady(result) }
                }
                .onFailure { error ->
                    _uiState.update {
                        DirectCommunicationEntryUiState.Error(
                            error.message ?: "No se pudo verificar la capacidad del dispositivo vinculado."
                        )
                    }
                }
        }
    }
}

@Composable
fun DirectCommunicationEntryScreen(
    fragileCitizenId: String,
    onNavigateToVideoCall: () -> Unit,
    onNavigateToVoiceCall: () -> Unit,
    onNavigateToDeviceStatusCheck: () -> Unit,
    onBackClick: () -> Unit,
    viewModel: DirectCommunicationRouterViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fragileCitizenId) {
        viewModel.evaluateAndInitiate(fragileCitizenId)
    }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is DirectCommunicationEntryUiState.RouteReady -> {
                when (state.routingResult) {
                    is DirectCommunicationRoutingResult.StartVideoCall -> onNavigateToVideoCall()
                    is DirectCommunicationRoutingResult.FallbackToVoiceCall -> onNavigateToVoiceCall()
                    is DirectCommunicationRoutingResult.TelemetryOnlyDevice -> onNavigateToDeviceStatusCheck()
                }
            }
            else -> Unit
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAF9)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val state = uiState) {
                is DirectCommunicationEntryUiState.Evaluating,
                is DirectCommunicationEntryUiState.RouteReady -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = Color(0xFF1E6B52))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Verificando capacidad del dispositivo vinculado...",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF5A6B65),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                is DirectCommunicationEntryUiState.Error -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Error de conexión",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF141E1B)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = state.message,
                                fontSize = 13.sp,
                                color = Color(0xFF6B7B75),
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                OutlinedButton(
                                    onClick = onBackClick,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Volver")
                                }
                                Button(
                                    onClick = { viewModel.evaluateAndInitiate(fragileCitizenId) },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E6B52))
                                ) {
                                    Text("Reintentar")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
