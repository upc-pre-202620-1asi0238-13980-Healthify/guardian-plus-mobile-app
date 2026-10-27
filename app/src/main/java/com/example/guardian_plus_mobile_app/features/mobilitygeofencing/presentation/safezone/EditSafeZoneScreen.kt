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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSafeZoneScreen(
    safeZoneId: String,
    initialName: String,
    initialLat: Double,
    initialLng: Double,
    initialRadius: Double,
    fragileCitizenId: String,
    onBackClick: () -> Unit,
    viewModel: SafeZoneViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf(initialName) }
    var latitude by remember { mutableStateOf(initialLat.toString()) }
    var longitude by remember { mutableStateOf(initialLng.toString()) }
    var radius by remember { mutableStateOf(initialRadius.toString()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Editar Zona Segura") },
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
                label = { Text("Nombre de la Zona") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitud Centro") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitud Centro") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = radius,
                onValueChange = { radius = it },
                label = { Text("Radio en Metros") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    val lat = latitude.toDoubleOrNull() ?: initialLat
                    val lng = longitude.toDoubleOrNull() ?: initialLng
                    val rad = radius.toDoubleOrNull() ?: initialRadius
                    viewModel.updateSafeZone(safeZoneId, name, lat, lng, rad, fragileCitizenId)
                    onBackClick()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Guardar Cambios")
            }
        }
    }
}