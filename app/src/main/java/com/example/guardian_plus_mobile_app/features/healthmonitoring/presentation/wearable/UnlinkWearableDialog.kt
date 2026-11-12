package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Confirmation before unlinking the wristband: from then on its readings stop reaching Guardian+. */
@Composable
fun UnlinkWearableDialog(
    modifier: Modifier = Modifier,
    serialNumber: String,
    careRecipientFirstName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(text = stringResource(R.string.wearable_unlink_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Text(
                text = stringResource(R.string.wearable_unlink_message, careRecipientFirstName, serialNumber),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.wearable_unlink_confirm),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.titleSmall
                )
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
private fun UnlinkWearableDialogPreview() {
    GuardianTheme(dynamicColor = false) {
        UnlinkWearableDialog(serialNumber = "GP-ESP32-S3-0100", careRecipientFirstName = "Elena", onConfirm = {}, onDismiss = {})
    }
}
