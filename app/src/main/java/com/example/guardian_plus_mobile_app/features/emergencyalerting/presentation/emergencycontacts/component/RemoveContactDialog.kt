package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts.component

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

/** Confirmation before removing a contact: from then on they stop receiving the alerts. */
@Composable
fun RemoveContactDialog(
    modifier: Modifier = Modifier,
    contactName: String,
    careRecipientFirstName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(text = stringResource(R.string.contacts_remove_title, contactName), style = MaterialTheme.typography.titleLarge) },
        text = {
            Text(
                text = stringResource(R.string.contacts_remove_message, careRecipientFirstName),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(R.string.contacts_remove_confirm),
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
private fun RemoveContactDialogPreview() {
    GuardianTheme(dynamicColor = false) {
        RemoveContactDialog(contactName = "Carlos Rojas", careRecipientFirstName = "Elena", onConfirm = {}, onDismiss = {})
    }
}
