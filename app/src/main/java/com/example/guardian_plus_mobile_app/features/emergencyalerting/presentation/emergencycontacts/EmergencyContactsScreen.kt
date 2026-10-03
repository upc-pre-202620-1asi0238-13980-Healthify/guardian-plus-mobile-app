package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.DemoSession
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.DetailTopBar
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts.component.AddContactSheet
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts.component.ContactCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts.component.RemoveContactDialog

@Composable
fun EmergencyContactsScreen(
    modifier: Modifier = Modifier,
    viewModel: EmergencyContactsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    // Rejections from the platform (e.g. removing the last contact) are shown once
    LaunchedEffect(uiState.actionErrorMessage) {
        uiState.actionErrorMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.onActionErrorShown()
        }
    }

    Box(modifier = modifier) {
        EmergencyContactsContent(
            uiState = uiState,
            onBackClick = onBackClick,
            onRetryClick = viewModel::loadContacts,
            onMoveUp = { contact -> viewModel.move(contact.id, -1) },
            onMoveDown = { contact -> viewModel.move(contact.id, 1) },
            onRemoveClick = viewModel::requestRemoval,
            onRemoveConfirm = viewModel::confirmRemoval,
            onRemoveDismiss = viewModel::cancelRemoval,
            onAddClick = viewModel::openAddSheet,
            onAddDismiss = viewModel::closeAddSheet,
            onAddSave = viewModel::addContact
        )
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun EmergencyContactsContent(
    modifier: Modifier = Modifier,
    uiState: EmergencyContactsUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onMoveUp: (EmergencyContact) -> Unit,
    onMoveDown: (EmergencyContact) -> Unit,
    onRemoveClick: (EmergencyContact) -> Unit,
    onRemoveConfirm: () -> Unit,
    onRemoveDismiss: () -> Unit,
    onAddClick: () -> Unit,
    onAddDismiss: () -> Unit,
    onAddSave: (String, String, String) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        DetailTopBar(
            title = stringResource(R.string.contacts_title),
            subtitle = DemoSession.CARE_RECIPIENT_NAME,
            onBackClick = onBackClick
        )

        when {
            uiState.isLoading && uiState.contacts.isEmpty() -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }

            uiState.errorMessage != null && uiState.contacts.isEmpty() -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = uiState.errorMessage,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                OutlinedButton(onClick = onRetryClick, modifier = Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.medium) {
                    Text(text = stringResource(R.string.action_retry))
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item(key = "explanation") {
                    Text(
                        text = if (uiState.contacts.isEmpty()) {
                            stringResource(R.string.contacts_empty, uiState.careRecipientFirstName)
                        } else {
                            stringResource(R.string.contacts_order_explanation, uiState.careRecipientFirstName, uiState.ackTimeoutSec)
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium)
                            .padding(14.dp)
                    )
                }
                itemsIndexed(uiState.contacts, key = { _, contact -> contact.id }) { index, contact ->
                    ContactCard(
                        modifier = Modifier.animateItem(),
                        contact = contact,
                        position = index,
                        isFirst = index == 0,
                        isLast = index == uiState.contacts.lastIndex,
                        canRemove = uiState.canRemove,
                        enabled = !uiState.isSaving,
                        onMoveUp = { onMoveUp(contact) },
                        onMoveDown = { onMoveDown(contact) },
                        onRemove = { onRemoveClick(contact) }
                    )
                }
                if (uiState.contacts.size == 1) {
                    item(key = "last-one-hint") {
                        Text(
                            text = stringResource(R.string.contacts_last_one_hint),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                item(key = "add") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Button(
                        onClick = onAddClick,
                        enabled = !uiState.isSaving,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(painter = painterResource(R.drawable.ic_user_plus), contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = stringResource(R.string.contacts_add))
                        }
                    }
                }
            }
        }
    }

    if (uiState.isAddSheetOpen) {
        AddContactSheet(
            careRecipientFirstName = uiState.careRecipientFirstName,
            isSaving = uiState.isAdding,
            onSave = onAddSave,
            onDismiss = onAddDismiss
        )
    }

    uiState.contactPendingRemoval?.let { contact ->
        RemoveContactDialog(
            contactName = contact.displayName,
            careRecipientFirstName = uiState.careRecipientFirstName,
            onConfirm = onRemoveConfirm,
            onDismiss = onRemoveDismiss
        )
    }
}

private val previewContacts = listOf(
    EmergencyContact("c1", "elena", "maria", "María Rojas", "Hija", "+51987654321", 1, true),
    EmergencyContact("c2", "elena", "carlos", "Carlos Rojas", "Hijo", "+51987654322", 2, true),
    EmergencyContact("c3", "elena", "ana", "Ana Torres", "Cuidadora", "+51987654323", 3, true)
)

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun EmergencyContactsContentPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(EmergencyContactsUiState(contacts = previewContacts, careRecipientFirstName = "Elena"))
    }
}

@Preview(showBackground = true)
@Composable
private fun EmergencyContactsContentLoadingPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(EmergencyContactsUiState(isLoading = true))
    }
}

@Preview(showBackground = true)
@Composable
private fun EmergencyContactsContentErrorPreview() {
    GuardianTheme(dynamicColor = false) {
        PreviewContent(EmergencyContactsUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."))
    }
}

@Composable
private fun PreviewContent(uiState: EmergencyContactsUiState) {
    EmergencyContactsContent(
        uiState = uiState,
        onBackClick = {},
        onRetryClick = {},
        onMoveUp = {},
        onMoveDown = {},
        onRemoveClick = {},
        onRemoveConfirm = {},
        onRemoveDismiss = {},
        onAddClick = {},
        onAddDismiss = {},
        onAddSave = { _, _, _ -> }
    )
}
