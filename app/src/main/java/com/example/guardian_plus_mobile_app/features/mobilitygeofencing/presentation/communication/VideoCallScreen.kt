package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.communication

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import java.util.Locale

@Composable
fun VideoCallScreen(
    fragileCitizenId: String,
    onBackClick: () -> Unit,
    onFallbackToVoice: () -> Unit = {},
    onCallUnanswered: () -> Unit = {},
    viewModel: VideoCallViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(fragileCitizenId) {
        viewModel.startVideoCall(fragileCitizenId)
    }

    LaunchedEffect(uiState.timedOut) {
        if (uiState.timedOut) {
            onCallUnanswered()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF8FAF9)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.endCall(onBackClick) },
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

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = uiState.citizenName,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF141E1B)
                    )
                    Text(
                        text = "En videollamada • ${uiState.deviceModel}",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7B75)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF1E6B52)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF63E6BE))
                        )
                        Spacer(modifier = Modifier.width(5.dp))
                        Text(
                            text = "EN VIVO",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Main Video Feed Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF0A1412),
                                Color(0xFF102420),
                                Color(0xFF08100E)
                            )
                        )
                    )
                    .padding(16.dp)
            ) {
                // Top-left BPM Badge
                Surface(
                    shape = RoundedCornerShape(50),
                    color = Color.White.copy(alpha = 0.92f),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "BPM",
                            tint = Color(0xFF1E6B52),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${uiState.vitals.bpm} bpm",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF141E1B)
                        )
                    }
                }

                // Top-right Self PiP Preview
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(width = 76.dp, height = 100.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color(0xFF192B38))
                        .border(1.dp, Color.White.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF284154)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Tú",
                                tint = Color(0xFF7FA3BE),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tú",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }

                // Center Citizen Video Avatar Representation
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(124.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1E463B).copy(alpha = 0.55f)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(92.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF2A5C4E).copy(alpha = 0.65f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (uiState.isCameraEnabled) Icons.Default.Person else Icons.Default.VideocamOff,
                            contentDescription = uiState.citizenName,
                            tint = Color(0xFF7CA99B),
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                // Bottom Overlay Info & Call Timer
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = uiState.citizenName,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = uiState.citizenRoleAndCode,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.65f)
                        )
                    }

                    Text(
                        text = formatDuration(uiState.elapsedSeconds),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Real-time Vitals Row (SpO2, Temp, Presión)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                VitalMetricCard(
                    label = "SpO₂",
                    value = "${uiState.vitals.spo2Percentage}%",
                    valueColor = Color(0xFF1E6B52),
                    modifier = Modifier.weight(1f)
                )
                VitalMetricCard(
                    label = "Temp.",
                    value = "${uiState.vitals.temperatureCelsius}°C",
                    valueColor = Color(0xFF141E1B),
                    modifier = Modifier.weight(1f)
                )
                VitalMetricCard(
                    label = "Presión",
                    value = uiState.vitals.bloodPressure,
                    valueColor = Color(0xFF141E1B),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Call Control Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = Color.White,
                shadowElevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mic Button
                    IconButton(
                        onClick = { viewModel.toggleMic() },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isMicMuted) Color(0xFFFFE5E5) else Color(0xFFF1F4F3))
                    ) {
                        Icon(
                            imageVector = if (uiState.isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Micrófono",
                            tint = if (uiState.isMicMuted) Color(0xFFD32F2F) else Color(0xFF26332E)
                        )
                    }

                    // Camera Button (Also allows testing degradation to Voice Call)
                    IconButton(
                        onClick = {
                            viewModel.toggleCamera()
                            onFallbackToVoice()
                        },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFF1F4F3))
                    ) {
                        Icon(
                            imageVector = if (uiState.isCameraEnabled) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = "Cambiar a llamada de voz",
                            tint = Color(0xFF26332E)
                        )
                    }

                    // Speaker Button
                    IconButton(
                        onClick = { viewModel.toggleSpeaker() },
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(if (uiState.isSpeakerEnabled) Color(0xFFC8EFE3) else Color(0xFFF1F4F3))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Altavoz",
                            tint = if (uiState.isSpeakerEnabled) Color(0xFF1E6B52) else Color(0xFF26332E)
                        )
                    }

                    // End Call Button
                    IconButton(
                        onClick = { viewModel.endCall(onBackClick) },
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFD9383A))
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "Finalizar llamada",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VitalMetricCard(
    label: String,
    value: String,
    valueColor: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        color = Color.White,
        tonalElevation = 1.dp,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
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
                color = valueColor
            )
        }
    }
}

private fun formatDuration(totalSeconds: Int): String {
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
}
