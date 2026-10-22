package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType

/**
 * Bottom sheet that links the care recipient's wearable by the serial number printed on it. Opened from the
 * "Sin pulsera vinculada" chip of Home; [onLinked] lets the screen reload its readings.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkWearableSheet(
    modifier: Modifier = Modifier,
    careRecipientFirstName: String,
    viewModel: LinkWearableViewModel = hiltViewModel(),
    onLinked: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isLinked) {
        if (uiState.isLinked) {
            viewModel.reset()
            onLinked()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.reset()
            onDismiss()
        },
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp)
                .navigationBarsPadding()
        ) {
            Text(text = stringResource(R.string.wearable_link_title), style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.wearable_link_subtitle, careRecipientFirstName),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = uiState.serialNumber,
                onValueChange = viewModel::onSerialNumberChange,
                label = { Text(stringResource(R.string.wearable_link_serial_label)) },
                placeholder = { Text(stringResource(R.string.wearable_link_serial_placeholder)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters, imeAction = ImeAction.Done),
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.wearable_link_type_label),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                DeviceType.entries.forEach { type ->
                    FilterChip(
                        selected = uiState.deviceType == type,
                        onClick = { viewModel.onDeviceTypeChange(type) },
                        label = { Text(stringResource(type.labelRes())) }
                    )
                }
            }
            uiState.errorMessage?.let {
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = viewModel::link,
                enabled = !uiState.isLoading,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.height(20.dp), color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text(stringResource(R.string.wearable_link_submit))
                }
            }
        }
    }
}

private fun DeviceType.labelRes(): Int = when (this) {
    DeviceType.WRISTBAND -> R.string.wearable_type_wristband
    DeviceType.SMARTWATCH -> R.string.wearable_type_smartwatch
    DeviceType.PATCH -> R.string.wearable_type_patch
}
