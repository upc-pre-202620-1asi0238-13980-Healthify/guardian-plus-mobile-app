package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone

import androidx.compose.foundation.layout.*
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeZonesScreen(
    fragileCitizenId: String = "123e4567-e89b-12d3-a456-426614174000",
    onBackClick: () -> Unit = {},
    viewModel: SafeZoneViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fragileCitizenId) {
        viewModel.loadActiveSafeZone(fragileCitizenId)
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
        }
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

                        val activeZone = state.activeZone
                        if (activeZone != null) {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = activeZone.name,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Radio: ${activeZone.radiusInMeters}m",
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                    Switch(
                                        checked = activeZone.status == "ACTIVE",
                                        onCheckedChange = { isChecked ->
                                            viewModel.toggleSafeZoneStatus(
                                                activeZone.id,
                                                !isChecked,
                                                fragileCitizenId
                                            )
                                        }
                                    )
                                }
                            }
                        } else {
                            Text("No hay ninguna zona segura activa configurada.")
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Button(
                            onClick = { /* Abrir modal/pantalla de creación */ },
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