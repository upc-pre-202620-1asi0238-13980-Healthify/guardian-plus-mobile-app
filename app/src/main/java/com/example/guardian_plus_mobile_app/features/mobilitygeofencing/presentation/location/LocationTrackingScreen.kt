package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import androidx.compose.foundation.background
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

@Composable
fun LocationTrackingScreen(
    fragileCitizenId: String = "123e4567-e89b-12d3-a456-426614174000",
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
                is LocationTrackingUiState.Error -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = state.message, color = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(onClick = { viewModel.loadCurrentLocation(fragileCitizenId) }) {
                            Text("Reintentar")
                        }
                    }
                }
                is LocationTrackingUiState.Success -> {
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
                        Spacer(modifier = Modifier.height(12.dp))

                        // Contenedor visual del mapa
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp)
                                .background(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Vista de Mapa (Geocerca/GPS)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarjeta de Datos de Ubicación
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Datos de ubicación",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("Latitud: ${state.location.latitude}")
                                Text("Longitud: ${state.location.longitude}")
                                Text("Precisión: ${state.location.accuracyInMeters} m")
                                Text("Estado: ${if (state.isWithinSafeZone) "En Zona Segura" else "Fuera de Zona Segura"}")
                            }
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Botones de acción inferiores
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