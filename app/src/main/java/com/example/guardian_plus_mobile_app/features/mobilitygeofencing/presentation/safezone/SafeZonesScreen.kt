package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeZonesScreen(
    fragileCitizenId: String,
    onBackClick: () -> Unit = {},
    onNavigateToCreate: () -> Unit = {},
    onNavigateToEdit: (id: String, name: String, lat: Double, lng: Double, radius: Double) -> Unit = { _, _, _, _, _ -> }, // 👈 1. Añadido parámetro de navegación para edición
    viewModel: SafeZoneViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarMessage by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(fragileCitizenId) {
        viewModel.loadSafeZones(fragileCitizenId)
    }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let { message ->
            scope.launch {
                snackbarHostState.showSnackbar(message)
                viewModel.clearMessage()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Zonas Seguras") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            when (val state = uiState) {
                is SafeZonesUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is SafeZonesUiState.Error -> {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                is SafeZonesUiState.Success -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Text(
                            text = "Perímetros Autorizados",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        val zones = state.zones
                        if (zones.isNotEmpty()) {
                            LazyColumn(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // 👈 2. Bloque de items integrado con acciones
                                items(zones) { zone ->
                                    Card(
                                        modifier = Modifier.fillMaxWidth(),
                                        shape = RoundedCornerShape(12.dp)
                                    ) {
                                        Column(modifier = Modifier.padding(16.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = zone.name,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                    // Nota: Agregué la Latitud/Longitud que tenías en tu diseño previo para no perder la información en pantalla
                                                    Text(
                                                        text = "Radio: ${zone.radiusInMeters}m | Lat: ${zone.latitude}",
                                                        style = MaterialTheme.typography.bodySmall
                                                    )
                                                }
                                                Switch(
                                                    checked = zone.status == "ACTIVE",
                                                    onCheckedChange = { isChecked ->
                                                        viewModel.toggleSafeZoneStatus(
                                                            zone.id,
                                                            !isChecked,
                                                            fragileCitizenId
                                                        )
                                                    }
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(8.dp))

                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.End
                                            ) {
                                                TextButton(onClick = {
                                                    onNavigateToEdit(
                                                        zone.id,
                                                        zone.name,
                                                        zone.latitude,
                                                        zone.longitude,
                                                        zone.radiusInMeters
                                                    )
                                                }) {
                                                    Text("Editar")
                                                }
                                                Spacer(modifier = Modifier.width(8.dp))
                                                TextButton(
                                                    onClick = { viewModel.deleteSafeZone(zone.id, fragileCitizenId) },
                                                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                                                ) {
                                                    Text("Eliminar")
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No hay ninguna zona segura registrada.")
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Button(
                            onClick = onNavigateToCreate,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Delimitar nueva zona")
                        }
                    }
                }
            }
        }
    }
}