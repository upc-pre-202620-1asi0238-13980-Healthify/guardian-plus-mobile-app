package com.example.guardian_plus_mobile_app.features.profile.presentation.carecircle

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R

@Composable
fun CareCircleScreen(
    modifier: Modifier = Modifier,
    viewModel: CareCircleViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CareCircleContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onSearchChange = viewModel::onSearchChange,
        onFilterChange = viewModel::onFilterChange,
        onRetry = viewModel::retry
    )
}

@Composable
private fun CareCircleContent(
    modifier: Modifier = Modifier,
    uiState: CareCircleUiState,
    onBackClick: () -> Unit,
    onSearchChange: (String) -> Unit,
    onFilterChange: (CareCircleFilter) -> Unit,
    onRetry: () -> Unit
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
            Arrangement.spacedBy(14.dp)
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
                    color =
                        MaterialTheme.colorScheme.surface
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
                        text = "Círculo de cuidado",
                        style =
                            MaterialTheme.typography.titleLarge
                    )

                    Text(
                        text = "Perfil de ${uiState.careRecipientName}",
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        item {
            Text(
                text = "Personas vinculadas que participan en el cuidado y seguimiento de ${uiState.careRecipientName}.",
                style = MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (uiState.isLoading) {
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

            return@LazyColumn
        }

        if (uiState.errorMessage != null) {
            item {
                Card(
                    onClick = onRetry,
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme
                                .colorScheme.errorContainer
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = uiState.errorMessage,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onErrorContainer
                        )

                        Text(
                            text = "Toca para intentar nuevamente",
                            style =
                                MaterialTheme
                                    .typography.bodySmall
                        )
                    }
                }
            }

            return@LazyColumn
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(40.dp),
                        shape = RoundedCornerShape(10.dp),
                        color =
                            MaterialTheme.colorScheme.surface
                    ) {
                        Box(
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = uiState.members.size
                                    .toString(),
                                style =
                                    MaterialTheme
                                        .typography.titleSmall,
                                color =
                                    MaterialTheme
                                        .colorScheme.primary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text =
                                "${uiState.members.size} personas vinculadas",
                            style =
                                MaterialTheme
                                    .typography.titleSmall
                        )

                        Text(
                            text =
                                "Familiares y cuidadores con acceso al perfil.",
                            style =
                                MaterialTheme
                                    .typography.bodySmall,
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .onSurfaceVariant
                        )
                    }
                }
            }
        }

        item {
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Buscar por nombre")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )
        }

        item {
            Row(
                horizontalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected =
                        uiState.selectedFilter ==
                                CareCircleFilter.ALL,
                    onClick = {
                        onFilterChange(
                            CareCircleFilter.ALL
                        )
                    },
                    label = {
                        Text("Todos")
                    }
                )

                FilterChip(
                    selected =
                        uiState.selectedFilter ==
                                CareCircleFilter.FAMILY,
                    onClick = {
                        onFilterChange(
                            CareCircleFilter.FAMILY
                        )
                    },
                    label = {
                        Text("Familiar")
                    }
                )

                FilterChip(
                    selected =
                        uiState.selectedFilter ==
                                CareCircleFilter.CAREGIVER,
                    onClick = {
                        onFilterChange(
                            CareCircleFilter.CAREGIVER
                        )
                    },
                    label = {
                        Text("Cuidador")
                    }
                )
            }
        }

        if (uiState.filteredMembers.isEmpty()) {
            item {
                Text(
                    text = "No se encontraron personas.",
                    style =
                        MaterialTheme.typography.bodyMedium,
                    color =
                        MaterialTheme
                            .colorScheme.onSurfaceVariant,
                    modifier =
                        Modifier.padding(vertical = 24.dp)
                )
            }
        } else {
            items(
                items = uiState.filteredMembers,
                key = { it.userId }
            ) { member ->
                CareCircleMemberCard(member)
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor =
                        MaterialTheme
                            .colorScheme.secondaryContainer
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
                        tint =
                            MaterialTheme
                                .colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "Las personas del círculo de cuidado participan en el seguimiento de ${uiState.careRecipientName} según su relación activa.",
                        style =
                            MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
private fun CareCircleMemberCard(
    member: CareCircleMemberUi
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline
        ),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(44.dp),
                shape = CircleShape,
                color =
                    MaterialTheme
                        .colorScheme.secondaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = member.initials,
                        color =
                            MaterialTheme
                                .colorScheme.primary,
                        style =
                            MaterialTheme
                                .typography.titleSmall
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = member.fullName,
                    style =
                        MaterialTheme.typography.titleSmall
                )

                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp),
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {
                    Text(
                        text = member.relationshipLabel,
                        style =
                            MaterialTheme.typography.bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    if (member.isActive) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer
                        ) {
                            Text(
                                text = "Activa",
                                style =
                                    MaterialTheme
                                        .typography.labelSmall,
                                color =
                                    MaterialTheme
                                        .colorScheme.primary,
                                modifier =
                                    Modifier.padding(
                                        horizontal = 7.dp,
                                        vertical = 2.dp
                                    )
                            )
                        }
                    }
                }
            }
        }
    }
}