package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.LocationHistoryItem
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LocationHistoryViewModel @Inject constructor(
    private val repository: MobilityGeofencingRepository
) : ViewModel() {
    private val _history = MutableStateFlow<List<LocationHistoryItem>>(emptyList())
    val history: StateFlow<List<LocationHistoryItem>> = _history.asStateFlow()

    fun loadHistory(fragileCitizenId: String) {
        viewModelScope.launch {
            repository.getLocationHistory(fragileCitizenId)
                .onSuccess { list ->
                    _history.value = list
                }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationHistoryScreen(
    fragileCitizenId: String,
    onBackClick: () -> Unit,
    viewModel: LocationHistoryViewModel = hiltViewModel()
) {
    val history by viewModel.history.collectAsState()

    LaunchedEffect(fragileCitizenId) {
        viewModel.loadHistory(fragileCitizenId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Historial de Ubicaciones") },
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
            if (history.isEmpty()) {
                Text(
                    text = "No hay registros en el historial.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(history) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Lat: ${item.latitude}, Lng: ${item.longitude}", fontWeight = FontWeight.Bold)
                                Text("Estado: ${item.status}")
                                Text("Fecha: ${item.recordedAt}", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}