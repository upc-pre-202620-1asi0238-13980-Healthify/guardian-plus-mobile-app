package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.wearable

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.DeviceType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.WearableDevice
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun WearableDetailsScreen(
    modifier: Modifier = Modifier,
    viewModel: WearableDetailsViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val careContext = LocalCareContext.current

    var showLinkSheet by remember {
        mutableStateOf(false)
    }

    WearableDetailsContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onLinkClick = {
            showLinkSheet = true
        },
        onRetry = viewModel::load
    )

    if (showLinkSheet) {
        LinkWearableSheet(
            careRecipientFirstName = careContext.careRecipientFirstName,
            onLinked = {
                showLinkSheet = false
                viewModel.load()
            },
            onDismiss = {
                showLinkSheet = false
            }
        )
    }
}

@Composable
private fun WearableDetailsContent(
    modifier: Modifier = Modifier,
    uiState: WearableDetailsUiState,
    onBackClick: () -> Unit,
    onLinkClick: () -> Unit,
    onRetry: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 20.dp,
            vertical = 14.dp
        ),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                    ),
                    color = MaterialTheme.colorScheme.surface
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
                        text = "Pulsera",
                        style = MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = uiState.careRecipientName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        when {
            uiState.isLoading -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }

            uiState.errorMessage != null -> {
                item {
                    Card(
                        onClick = onRetry,
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Text(
                                text = uiState.errorMessage,
                                color =
                                    MaterialTheme.colorScheme.onErrorContainer
                            )

                            Text(
                                text = "Toca para intentar nuevamente",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }

            uiState.wearable == null -> {
                item {
                    EmptyWearableCard(
                        careRecipientName = uiState.careRecipientName,
                        onLinkClick = onLinkClick
                    )
                }
            }

            else -> {
                item {
                    WearableSummaryCard(
                        wearable = uiState.wearable,
                        careRecipientName = uiState.careRecipientName
                    )
                }

                item {
                    Text(
                        text = "INFORMACIÓN DEL DISPOSITIVO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    WearableInformationCard(
                        wearable = uiState.wearable
                    )
                }

                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Dispositivo vinculado",
                                style = MaterialTheme.typography.titleSmall
                            )

                            Text(
                                text = "Este dispositivo está asociado al perfil de ${uiState.careRecipientName}.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                item {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor =
                                MaterialTheme.colorScheme.secondaryContainer
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Info,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Text(
                                text = "Guardian+ utiliza los dispositivos vinculados para identificar qué lecturas pertenecen a ${uiState.careRecipientName}.",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun WearableSummaryCard(
    wearable: WearableDevice,
    careRecipientName: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Surface(
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            R.drawable.ic_watch
                        ),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = wearable.deviceType.displayName(),
                        style = MaterialTheme.typography.titleSmall
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Vinculada",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(
                                horizontal = 8.dp,
                                vertical = 3.dp
                            )
                        )
                    }
                }

                Text(
                    text = "Vinculada a $careRecipientName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WearableInformationCard(
    wearable: WearableDevice
) {
    val formatter =
        DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")

    val linkedAt =
        wearable.linkedAt
            .atZone(ZoneId.systemDefault())
            .format(formatter)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            WearableInfoRow(
                label = "Tipo",
                value = wearable.deviceType.displayName()
            )

            WearableInfoRow(
                label = "Identificador",
                value = wearable.serialNumber
            )

            WearableInfoRow(
                label = "Vinculada desde",
                value = linkedAt
            )
        }
    }
}

@Composable
private fun WearableInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun EmptyWearableCard(
    careRecipientName: String,
    onLinkClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Sin dispositivo vinculado",
                style = MaterialTheme.typography.titleSmall
            )

            Text(
                text = "$careRecipientName todavía no tiene un dispositivo asociado.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Button(
                onClick = onLinkClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Vincular dispositivo")
            }
        }
    }
}

private fun DeviceType.displayName(): String =
    when (this) {
        DeviceType.WRISTBAND -> "Guardian+ Band"
        DeviceType.SMARTWATCH -> "Smartwatch"
        DeviceType.PATCH -> "Patch"
    }