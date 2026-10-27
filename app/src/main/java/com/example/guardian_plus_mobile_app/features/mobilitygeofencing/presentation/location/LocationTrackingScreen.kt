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
                else -> {
                    // Si es Success o Error con fallback, mostramos la interfaz completa
                    val location = (state as? LocationTrackingUiState.Success)?.location
                    val isWithin = (state as? LocationTrackingUiState.Success)?.isWithinSafeZone ?: true

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

                        // ¡AQUÍ SE INTEGRÓ EL MAPA REAL EN LUGAR DE LA CAJA GRIS!
                        MapViewComposable(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(220.dp),
                            latitude = location?.latitude ?: -12.0463,
                            longitude = location?.longitude ?: -77.0427,
                            isWithinSafeZone = isWithin,
                            safeZoneRadius = 100.0 // O el radio real que obtengas de tu API de Zonas Seguras
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarjeta de Datos de Ubicación
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
                                Text("LATITUD: ${location?.latitude ?: -12.0463}")
                                Text("LONGITUD: ${location?.longitude ?: -77.0427}")
                                Text("PRECISIÓN: ${location?.accuracyInMeters ?: 12.0} m")
                                Text("ACTUALIZACIÓN: ${location?.recordedAt ?: "Reciente"}")
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
