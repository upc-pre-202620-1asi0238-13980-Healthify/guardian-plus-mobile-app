package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun DeviceStatusCheckScreen(
    fragileCitizenId: String,
    onBackClick: () -> Unit,
    viewModel: DeviceStatusCheckViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(fragileCitizenId) {
        viewModel.loadDeviceStatus(fragileCitizenId)
    }

    val dialPhoneNumber: (String) -> Unit = { phone ->
        val cleanPhone = phone.replace(" ", "")
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$cleanPhone"))
        runCatching { context.startActivity(intent) }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAF9)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFEEF2F0))
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color(0xFF1B2522),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Verificación de estado",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF141E1B)
                    )
                    Text(
                        text = "Canal de contacto",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7B75)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Status Card
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                border = BorderStroke(1.dp, Color(0xFFE5ECE9))
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    // Device Capability Restriction Section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFFDDF3EC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Watch,
                                contentDescription = null,
                                tint = Color(0xFF1E6B52),
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color(0xFFFFF4D9)
                            ) {
                                Text(
                                    text = "⚠ ${uiState.capabilities.modelName} — Solo Telemetría",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF96610E),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = "La pulsera vinculada solo admite alertas hápticas y telemetría. No dispone de altavoz ni micrófono para llamadas.",
                                fontSize = 12.sp,
                                lineHeight = 16.sp,
                                color = Color(0xFF5C6B66)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Hardware Capability Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CapabilityBadge(text = "✓ Telemetría", isSupported = true)
                        CapabilityBadge(text = "✓ Alertas hápticas", isSupported = true)
                        CapabilityBadge(text = "✕ Altavoz", isSupported = false)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CapabilityBadge(text = "✕ Micrófono", isSupported = false)
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color(0xFFEDF2F0)
                    )

                    // Last Received Metrics Section
                    Text(
                        text = "ÚLTIMAS MÉTRICAS RECIBIDAS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF6B7B75),
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        TelemetryStatColumn(
                            label = "BPM",
                            value = "${uiState.lastVitals.bpm}"
                        )
                        TelemetryStatColumn(
                            label = "SpO₂",
                            value = "${uiState.lastVitals.spo2Percentage}%"
                        )
                        TelemetryStatColumn(
                            label = "Temp.",
                            value = "${uiState.lastVitals.temperatureCelsius}°C"
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = Color(0xFFEDF2F0)
                    )

                    // Phone Contact Options Section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            tint = Color(0xFF1E6B52),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Opciones de contacto telefónico",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2B3A35)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Option 1: Citizen Mobile Phone
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Llamar al móvil de Elena",
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF141E1B)
                        )

                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFFDDF3EC)
                        ) {
                            Text(
                                text = "• Disponible",
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E6B52),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = uiState.capabilities.citizenPhoneNumber,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7B75)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { dialPhoneNumber(uiState.capabilities.citizenPhoneNumber) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF1E6B52),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Llamar a línea celular",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Option 2: Companion / Caregiver Phone
                    Text(
                        text = "Llamar a acompañante / cuidador",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF141E1B)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDDF3EC)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "RG",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E6B52)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "${uiState.capabilities.caregiverName} • ${uiState.capabilities.caregiverRole}",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF2B3A35)
                            )
                            Text(
                                text = uiState.capabilities.caregiverPhoneNumber,
                                fontSize = 11.5.sp,
                                color = Color(0xFF6B7B75)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = { dialPhoneNumber(uiState.capabilities.caregiverPhoneNumber) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp),
                        shape = RoundedCornerShape(22.dp),
                        border = BorderStroke(1.dp, Color(0xFF1E6B52)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF1E6B52)
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Llamar a cuidadora",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CapabilityBadge(
    text: String,
    isSupported: Boolean
) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = if (isSupported) Color(0xFFE3F6EF) else Color(0xFFFDE8E8)
    ) {
        Text(
            text = text,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isSupported) Color(0xFF1E6B52) else Color(0xFFC53030),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun TelemetryStatColumn(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFF7A8983)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1E6B52)
        )
    }
}
