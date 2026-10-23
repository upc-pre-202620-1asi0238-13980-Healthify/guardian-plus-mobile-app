package com.example.guardian_plus_mobile_app.features.profile.presentation.carerecipient

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R

@Composable
fun CareRecipientScreen(
    modifier: Modifier = Modifier,
    viewModel: CareRecipientViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }

    LaunchedEffect(
        uiState.errorMessage,
        uiState.successMessage
    ) {
        val message =
            uiState.errorMessage ?: uiState.successMessage

        if (message != null) {
            snackbar.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Scaffold(
        modifier = modifier,
        snackbarHost = {
            SnackbarHost(snackbar)
        }
    ) { innerPadding ->

        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            CareRecipientContent(
                modifier = Modifier.padding(innerPadding),
                uiState = uiState,
                onBackClick = onBackClick,
                onEditClick = viewModel::startEditing,
                onCancelEdit = viewModel::cancelEditing,
                onSaveClick = viewModel::save,
                onFirstNameChange = viewModel::onFirstNameChange,
                onLastNameChange = viewModel::onLastNameChange,
                onBirthDateChange = viewModel::onBirthDateChange
            )
        }
    }
}

@Composable
private fun CareRecipientContent(
    modifier: Modifier = Modifier,
    uiState: CareRecipientUiState,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onCancelEdit: () -> Unit,
    onSaveClick: () -> Unit,
    onFirstNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onBirthDateChange: (String) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                horizontal = 20.dp,
                vertical = 14.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(16.dp)
    ) {

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            painter = painterResource(
                                R.drawable.ic_chevron_left
                            ),
                            contentDescription = "Volver"
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Persona bajo cuidado",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Perfil de la persona",
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme.colorScheme
                                .onSurfaceVariant
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Surface(
                        modifier = Modifier.size(48.dp),
                        shape = CircleShape,
                        color = MaterialTheme
                            .colorScheme
                            .secondaryContainer
                    ) {
                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Text(
                                text = uiState.firstName
                                    .take(1)
                                    .uppercase(),
                                color =
                                    MaterialTheme
                                        .colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = uiState.fullName,
                            style =
                                MaterialTheme
                                    .typography.titleSmall
                        )

                        Text(
                            text = "Persona bajo cuidado",
                            style =
                                MaterialTheme
                                    .typography.bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }

                    if (!uiState.isEditing) {
                        TextButton(
                            onClick = onEditClick
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Outlined.Edit,
                                contentDescription = null,
                                modifier =
                                    Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier =
                                    Modifier.width(4.dp)
                            )

                            Text("Editar")
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text(
                    text = "INFORMACIÓN PERSONAL",
                    style =
                        MaterialTheme.typography.labelSmall,
                    color =
                        MaterialTheme
                            .colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier.padding(
                            start = 4.dp,
                            bottom = 8.dp
                        )
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {

                        if (uiState.isEditing) {

                            OutlinedTextField(
                                value = uiState.firstName,
                                onValueChange =
                                    onFirstNameChange,
                                label = {
                                    Text("Nombre")
                                },
                                singleLine = true,
                                modifier =
                                    Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = uiState.lastName,
                                onValueChange =
                                    onLastNameChange,
                                label = {
                                    Text("Apellidos")
                                },
                                singleLine = true,
                                modifier =
                                    Modifier.fillMaxWidth()
                            )

                            OutlinedTextField(
                                value = uiState.birthDate,
                                onValueChange =
                                    onBirthDateChange,
                                label = {
                                    Text(
                                        "Fecha de nacimiento"
                                    )
                                },
                                supportingText = {
                                    Text("dd/MM/yyyy")
                                },
                                singleLine = true,
                                modifier =
                                    Modifier.fillMaxWidth()
                            )

                        } else {

                            InfoRow(
                                label = "Nombre completo",
                                value = uiState.fullName
                            )

                            InfoRow(
                                label = "Fecha de nacimiento",
                                value = uiState.birthDate
                            )
                        }
                    }
                }
            }
        }

        item {
            Column {
                Text(
                    text = "RELACIÓN DE CUIDADO",
                    style =
                        MaterialTheme.typography.labelSmall,
                    color =
                        MaterialTheme
                            .colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier.padding(
                            start = 4.dp,
                            bottom = 8.dp
                        )
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(14.dp)
                    ) {

                        InfoRow(
                            label = "Tu relación",
                            value = uiState.relationshipLabel
                        )

                        InfoRow(
                            label = "Estado",
                            value =
                                if (
                                    uiState.isRelationshipActive
                                ) {
                                    "Activa"
                                } else {
                                    "Finalizada"
                                }
                        )
                    }
                }
            }
        }

        if (uiState.isEditing) {
            item {
                Button(
                    onClick = onSaveClick,
                    enabled = !uiState.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    if (uiState.isSaving) {
                        CircularProgressIndicator(
                            modifier =
                                Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color =
                                MaterialTheme
                                    .colorScheme.onPrimary
                        )
                    } else {
                        Text("Guardar cambios")
                    }
                }
            }

            item {
                TextButton(
                    onClick = onCancelEdit,
                    enabled = !uiState.isSaving,
                    modifier =
                        Modifier.fillMaxWidth()
                ) {
                    Text("Cancelar")
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme
                            .secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment =
                        Alignment.Top
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.Info,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint =
                            MaterialTheme
                                .colorScheme.primary
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text = "La información de este perfil identifica a la persona bajo cuidado dentro de Guardian+.",
                        style =
                            MaterialTheme
                                .typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color =
                MaterialTheme
                    .colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}