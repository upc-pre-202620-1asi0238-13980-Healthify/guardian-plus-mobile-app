package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.safezone

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.repository.MobilityGeofencingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateSafeZoneViewModel @Inject constructor(
    private val repository: MobilityGeofencingRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<Boolean>(false)
    val uiState: StateFlow<Boolean> = _uiState.asStateFlow()

    fun createSafeZone(
        fragileCitizenId: String,
        name: String,
        lat: Double,
        lng: Double,
        radius: Double,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            repository.createSafeZone(fragileCitizenId, name, lat, lng, radius)
                .onSuccess {
                    onSuccess()
                }
                .onFailure { exception ->
                    onError(exception.localizedMessage ?: "Error desconocido al guardar")
                }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateSafeZoneScreen(
    fragileCitizenId: String,
    onBackClick: () -> Unit,
    viewModel: CreateSafeZoneViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("-12.046374") }
    var longitude by remember { mutableStateOf("-77.042793") }
    var radius by remember { mutableStateOf("100.0") }

    // 👈 NUEVOS ESTADOS INTEGRADOS
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Delimitar Nueva Zona Segura") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la Zona (Ej: Hogar)") },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitud Centro") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitud Centro") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            OutlinedTextField(
                value = radius,
                onValueChange = { radius = it },
                label = { Text("Radio en Metros") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            )

            // 👈 MOSTRAR MENSAJE DE ERROR SI EXISTE
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val lat = latitude.toDoubleOrNull()
                    val lng = longitude.toDoubleOrNull()
                    val rad = radius.toDoubleOrNull()

                    if (name.isBlank() || lat == null || lng == null || rad == null) {
                        errorMessage = "Por favor ingresa datos válidos."
                        return@Button
                    }

                    isLoading = true
                    errorMessage = null

                    viewModel.createSafeZone(
                        fragileCitizenId = fragileCitizenId,
                        name = name,
                        lat = lat,
                        lng = lng,
                        radius = rad,
                        onSuccess = {
                            isLoading = false
                            onBackClick()
                        },
                        onError = { err ->
                            isLoading = false
                            errorMessage = err
                        }
                    )
                },
                enabled = !isLoading,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Guardar Zona Segura")
                }
            }
        }
    }
}
