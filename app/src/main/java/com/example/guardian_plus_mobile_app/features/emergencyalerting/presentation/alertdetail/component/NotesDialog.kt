package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

private const val MAX_NOTES_LENGTH = 2_000

/** Confirmation for stabilizing or closing an incident, with optional notes that the backend stores on it. */
@Composable
fun NotesDialog(
    modifier: Modifier = Modifier,
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: (notes: String) -> Unit,
    onDismiss: () -> Unit
) {
    // The text being typed is purely visual state, so it lives here and not in the ViewModel
    var notes by rememberSaveable { mutableStateOf("") }

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it.take(MAX_NOTES_LENGTH) },
                    label = { Text(text = stringResource(R.string.notes_dialog_label)) },
                    placeholder = { Text(text = stringResource(R.string.notes_dialog_placeholder)) },
                    shape = MaterialTheme.shapes.medium,
                    minLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(notes.trim()) }) {
                Text(text = confirmLabel, style = MaterialTheme.typography.titleSmall)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Preview
@Composable
private fun NotesDialogPreview() {
    GuardianTheme(dynamicColor = false) {
        NotesDialog(
            title = "Cerrar incidente",
            message = "La alerta quedará resuelta y pasará al historial.",
            confirmLabel = "Cerrar incidente",
            onConfirm = {},
            onDismiss = {}
        )
    }
}
