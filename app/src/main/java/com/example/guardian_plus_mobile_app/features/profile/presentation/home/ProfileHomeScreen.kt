package com.example.guardian_plus_mobile_app.features.profile.presentation.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.Destructive
import com.example.guardian_plus_mobile_app.core.designsystem.theme.DestructivePastel
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.session.LocalCareContext
import com.example.guardian_plus_mobile_app.core.text.initials

@Composable
fun ProfileHomeScreen(
    modifier: Modifier = Modifier,
    viewModel: ProfileHomeViewModel = hiltViewModel(),
    onBackClick: () -> Unit,
    onPersonalDataClick: () -> Unit = {},
    onCareRecipientClick: () -> Unit = {},
    onCareCircleClick: () -> Unit = {},
    onWearableClick: () -> Unit = {},
    onPlanClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val careContext = LocalCareContext.current
    var showLogoutDialog by rememberSaveable { mutableStateOf(false) }

    ProfileHomeContent(
        modifier = modifier,
        userName = careContext.userFullName,
        userInitials = careContext.userFullName.initials(),
        relationshipLabel = uiState.relationshipLabel,
        careRecipientName = careContext.careRecipientName,
        onBackClick = onBackClick,
        onPersonalDataClick = onPersonalDataClick,
        onCareRecipientClick = onCareRecipientClick,
        onCareCircleClick = onCareCircleClick,
        onWearableClick = onWearableClick,
        onPlanClick = onPlanClick,
        onSettingsClick = onSettingsClick,
        onLogoutClick = { showLogoutDialog = true }
    )

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            icon = {
                Surface(
                    color = DestructivePastel,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Logout,
                        contentDescription = null,
                        tint = Destructive,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            },
            title = {
                Text(
                    text = "Cerrar sesión",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "¿Deseas cerrar tu sesión en Guardian+? Dejarás de ver la información de ${careContext.careRecipientFirstName} hasta que vuelvas a iniciar sesión."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }
                ) {
                    Text(
                        text = "Cerrar sesión",
                        color = Destructive
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun ProfileHomeContent(
    modifier: Modifier = Modifier,
    userName: String,
    userInitials: String,
    relationshipLabel: String,
    careRecipientName: String,
    onBackClick: () -> Unit,
    onPersonalDataClick: () -> Unit,
    onCareRecipientClick: () -> Unit,
    onCareCircleClick: () -> Unit,
    onWearableClick: () -> Unit,
    onPlanClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            ProfileHeader(onBackClick = onBackClick)
        }

        item {
            ProfileSummaryCard(
                name = userName,
                initials = userInitials,
                relationshipLabel = relationshipLabel,
                careRecipientName = careRecipientName,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }

        item {
            ProfileSection(
                title = "CUENTA",
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Outlined.Person,
                    title = "Mis datos",
                    subtitle = "Información personal y contacto",
                    onClick = onPersonalDataClick
                )
            }
        }

        item {
            ProfileSection(
                title = "CUIDADO",
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                ProfileMenuItem(
                    iconRes = R.drawable.ic_heart,
                    title = "Persona bajo cuidado",
                    subtitle = "Información de $careRecipientName",
                    onClick = onCareRecipientClick
                )

                ProfileMenuItem(
                    iconRes = R.drawable.ic_users,
                    title = "Círculo de cuidado",
                    subtitle = "Personas que participan en su cuidado",
                    onClick = onCareCircleClick
                )
            }
        }

        item {
            ProfileSection(
                title = "DISPOSITIVO Y PLAN",
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                ProfileMenuItem(
                    iconRes = R.drawable.ic_watch,
                    title = "Pulsera",
                    subtitle = "Estado y preferencias del dispositivo",
                    onClick = onWearableClick
                )

                ProfileMenuItem(
                    icon = Icons.Outlined.StarOutline,
                    title = "Mi plan",
                    subtitle = "Suscripción y beneficios actuales",
                    onClick = onPlanClick
                )
            }
        }

        item {
            ProfileSection(
                title = "PREFERENCIAS",
                modifier = Modifier.padding(horizontal = 20.dp)
            ) {
                ProfileMenuItem(
                    icon = Icons.Outlined.Settings,
                    title = "Configuración",
                    subtitle = "Preferencias de la aplicación",
                    onClick = onSettingsClick
                )
            }
        }

        item {
            Card(
                onClick = onLogoutClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = DestructivePastel,
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Logout,
                            contentDescription = null,
                            tint = Destructive,
                            modifier = Modifier.padding(9.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Cerrar sesión",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Destructive
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun ProfileHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = CircleShape,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            color = MaterialTheme.colorScheme.surface
        ) {
            IconButton(
                onClick = onBackClick,
                modifier = Modifier.size(44.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_chevron_left),
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "Perfil",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun ProfileSummaryCard(
    name: String,
    initials: String,
    relationshipLabel: String,
    careRecipientName: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
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
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = initials,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleSmall
                    )

                    if (relationshipLabel.isNotBlank()) {
                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            color = MaterialTheme.colorScheme.secondaryContainer,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = relationshipLabel,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(
                                    horizontal = 8.dp,
                                    vertical = 3.dp
                                )
                            )
                        }
                    }
                }

                Text(
                    text = "Cuida de $careRecipientName",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ProfileSection(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(
                start = 4.dp,
                bottom = 8.dp
            )
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            content()
        }
    }
}

@Composable
private fun ProfileMenuItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    icon: ImageVector? = null,
    iconRes: Int? = null
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                when {
                    icon != null -> {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(9.dp)
                        )
                    }

                    iconRes != null -> {
                        Icon(
                            painter = painterResource(iconRes),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(9.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun ProfileHomePreview() {
    GuardianTheme(dynamicColor = false) {
        ProfileHomeContent(
            userName = "María Rojas",
            userInitials = "MR",
            relationshipLabel = "Familiar",
            careRecipientName = "Elena Rojas",
            onBackClick = {},
            onPersonalDataClick = {},
            onCareRecipientClick = {},
            onCareCircleClick = {},
            onWearableClick = {},
            onPlanClick = {},
            onSettingsClick = {},
            onLogoutClick = {}
        )
    }
}