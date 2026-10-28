package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun LocationTrackingScreen(
    fragileCitizenId: String,
    onNavigateToHistory: () -> Unit = {},
    onNavigateToSafeZones: () -> Unit = {},
    viewModel: LocationTrackingViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fragileCitizenId) {
        viewModel.loadCurrentLocation(fragileCitizenId)
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = uiState) {
                is LocationTrackingUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Ubicación en tiempo real",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        when (state) {
                            is LocationTrackingUiState.Success -> LocationContent(state)
                            is LocationTrackingUiState.Error -> NoLocationCard(
                                message = state.message,
                                onRetry = { viewModel.loadCurrentLocation(fragileCitizenId) }
                            )
                            else -> Unit
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            OutlinedButton(
                                onClick = onNavigateToHistory,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.History, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Historial")
                            }

                            Button(
                                onClick = onNavigateToSafeZones,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Security, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Geocercas")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LocationContent(state: LocationTrackingUiState.Success) {
    val location = state.location
    val safeZone = state.activeSafeZone

    MapViewComposable(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp),
        latitude = location.latitude,
        longitude = location.longitude,
        isWithinSafeZone = state.isWithinSafeZone,
        safeZone = safeZone
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Inside or outside is only meaningful against an active safe zone
    val (statusText, statusColor) = when {
        safeZone == null -> "Sin zona segura activa" to MaterialTheme.colorScheme.onSurfaceVariant
        state.isWithinSafeZone -> "Dentro de la zona segura \"${safeZone.name}\"" to Color(0xFF2E7D32)
        else -> "Fuera de la zona segura \"${safeZone.name}\"" to MaterialTheme.colorScheme.error
    }
    Text(text = statusText, color = statusColor, fontWeight = FontWeight.SemiBold)

    Spacer(modifier = Modifier.height(12.dp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Datos de ubicación",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            Text("LATITUD: ${location.latitude}")
            Text("LONGITUD: ${location.longitude}")
            Text("PRECISIÓN: ${location.accuracyInMeters} m")
            Text("ACTUALIZACIÓN: ${formatRecordedAt(location.recordedAt)}")
        }
    }
}

// Shown instead of invented coordinates while the wristband has not sent a location yet
@Composable
private fun NoLocationCard(message: String, onRetry: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Ubicación no disponible", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = onRetry) { Text("Reintentar") }
        }
    }
}

private val recordedAtFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

private fun formatRecordedAt(recordedAt: String): String =
    runCatching { recordedAtFormatter.format(Instant.parse(recordedAt).atZone(ZoneId.systemDefault())) }
        .getOrDefault(recordedAt.ifBlank { "Sin registro" })
