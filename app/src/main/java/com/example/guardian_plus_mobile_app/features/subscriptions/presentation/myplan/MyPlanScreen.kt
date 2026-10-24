package com.example.guardian_plus_mobile_app.features.subscriptions.presentation.myplan

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun MyPlanScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onViewPlansClick: () -> Unit = {},
    onManageSubscriptionClick: () -> Unit = {},
    viewModel: MyPlanViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    MyPlanContent(
        modifier = modifier,
        uiState = uiState,
        onBackClick = onBackClick,
        onViewPlansClick = onViewPlansClick,
        onManageSubscriptionClick = onManageSubscriptionClick,
        onCancelSubscriptionClick = viewModel::requestCancellation
    )
}


@Composable
private fun MyPlanContent(
    modifier: Modifier = Modifier,
    uiState: MyPlanUiState,
    onBackClick: () -> Unit,
    onViewPlansClick: () -> Unit,
    onManageSubscriptionClick: () -> Unit,
    onCancelSubscriptionClick: () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {

            item {
                Spacer(modifier = Modifier.height(10.dp))

                MyPlanHeader(
                    onBackClick = onBackClick
                )
            }

            when {
                uiState.isLoading -> {
                    item {
                        LoadingContent()
                    }
                }

                uiState.errorMessage != null -> {
                    item {
                        ErrorCard(
                            message = uiState.errorMessage
                        )
                    }
                }

                !uiState.hasSubscription -> {
                    item {
                        NoSubscriptionContent(
                            onViewPlansClick = onViewPlansClick
                        )
                    }
                }

                else -> {

                    item {
                        SectionTitle(
                            text = "PLAN ACTUAL"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        CurrentPlanCard(
                            uiState = uiState
                        )
                    }

                    item {
                        SectionTitle(
                            text = "BENEFICIOS INCLUIDOS"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        BenefitsCard(
                            benefits = uiState.benefits
                        )
                    }

                    item {
                        SectionTitle(
                            text = "SUSCRIPCIÓN"
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        SubscriptionDetailsCard(
                            uiState = uiState
                        )
                    }

                    item {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {

                            Button(
                                onClick = onViewPlansClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor =
                                        MaterialTheme.colorScheme.primary,
                                    contentColor =
                                        MaterialTheme.colorScheme.onPrimary
                                )
                            ) {
                                Text(
                                    text = "Ver otros planes",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            OutlinedButton(
                                onClick = onManageSubscriptionClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(52.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = BorderStroke(
                                    width = 1.dp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            ) {
                                Text(
                                    text = "Gestionar suscripción",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    item {
                        InformationCard(
                            text = "Los beneficios disponibles dependen del plan activo y pueden actualizarse cuando cambies de suscripción."
                        )
                    }

                    if (
                        uiState.statusLabel == "Activo" ||
                        uiState.statusLabel == "Pendiente"
                    ) {
                        item {
                            CancelSubscriptionButton(
                                isLoading = uiState.isCancelling,
                                onClick = onCancelSubscriptionClick
                            )
                        }
                    }

                    uiState.cancellationMessage?.let { message ->
                        item {
                            SuccessMessageCard(
                                message = message
                            )
                        }
                    }

                    uiState.cancellationError?.let { message ->
                        item {
                            ErrorCard(
                                message = message
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}


@Composable
private fun MyPlanHeader(
    onBackClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            onClick = onBackClick,
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outlineVariant
            ),
            modifier = Modifier.size(42.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "Volver",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column {
            Text(
                text = "Mi plan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Suscripción Guardian+",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun CurrentPlanCard(
    uiState: MyPlanUiState
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "PLAN ACTUAL",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = uiState.planName.ifBlank {
                            "Guardian+"
                        },
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold
                    )
                }

                StatusChip(
                    text = uiState.statusLabel.ifBlank {
                        "Activo"
                    }
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = uiState.description.ifBlank {
                    "Protección y seguimiento para el círculo de cuidado"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}


@Composable
private fun StatusChip(
    text: String
) {
    Surface(
        shape = RoundedCornerShape(50.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = 12.dp,
                vertical = 6.dp
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
private fun BenefitsCard(
    benefits: List<String>
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 14.dp
            ),
            verticalArrangement = Arrangement.spacedBy(13.dp)
        ) {

            if (benefits.isEmpty()) {
                Text(
                    text = "No hay beneficios disponibles para mostrar.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                benefits.forEach { benefit ->
                    BenefitRow(
                        text = benefit
                    )
                }
            }
        }
    }
}


@Composable
private fun BenefitRow(
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.size(24.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Outlined.Check,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(11.dp))

        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}


@Composable
private fun SubscriptionDetailsCard(
    uiState: MyPlanUiState
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Column {

            DetailRow(
                label = "Estado",
                value = uiState.statusLabel.ifBlank {
                    "—"
                }
            )

            DetailDivider()

            DetailRow(
                label = "Ciclo",
                value = uiState.billingCycle.ifBlank {
                    "—"
                }
            )

            DetailDivider()

            DetailRow(
                label = "Renovación",
                value = when {
                    uiState.statusLabel ==
                            "Cancelación programada" ->
                        "No renovará"

                    uiState.automaticRenewal ->
                        "Automática"

                    else ->
                        "Manual"
                }
            )
        }
    }
}


@Composable
private fun DetailRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 15.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.SemiBold
        )
    }
}


@Composable
private fun DetailDivider() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp),
        color = MaterialTheme.colorScheme.outlineVariant
    ) {}
}


@Composable
private fun InformationCard(
    text: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
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

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = text,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}


@Composable
private fun CancelSubscriptionButton(
    isLoading: Boolean,
    onClick: () -> Unit
) {
    TextButton(
        onClick = onClick,
        enabled = !isLoading,
        modifier = Modifier.fillMaxWidth()
    ) {

        if (isLoading) {

            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "Procesando...",
                color = MaterialTheme.colorScheme.error
            )

        } else {

            Icon(
                imageVector = Icons.Outlined.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(17.dp)
            )

            Spacer(modifier = Modifier.width(7.dp))

            Text(
                text = "Cancelar suscripción",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}


@Composable
private fun SuccessMessageCard(
    message: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer
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

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = message,
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}


@Composable
private fun ErrorCard(
    message: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.errorContainer
    ) {
        Text(
            text = message,
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}


@Composable
private fun LoadingContent() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Cargando tu suscripción...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


@Composable
private fun NoSubscriptionContent(
    onViewPlansClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(
                1.dp,
                MaterialTheme.colorScheme.outlineVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {

                Text(
                    text = "Aún no tienes un plan activo",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Elige un plan Guardian+ para acceder a los beneficios de cuidado, seguimiento y monitoreo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onViewPlansClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Ver planes disponibles",
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Bold
    )
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFFF7FAF8
)
@Composable
private fun MyPlanLoadedPreview() {
    MaterialTheme {
        MyPlanContent(
            uiState = MyPlanUiState(
                subscriptionId = "subscription-01",
                planName = "Familiar",
                statusLabel = "Activo",
                description =
                    "Protección y seguimiento para el círculo de cuidado",
                benefits = listOf(
                    "Monitoreo remoto",
                    "Alertas y gestión de emergencias",
                    "Seguimiento de ubicación",
                    "Gestión de rutinas de cuidado",
                    "Acceso para el círculo de cuidado"
                ),
                billingCycle = "Mensual",
                automaticRenewal = true,
                hasSubscription = true,
                isLoading = false
            ),
            onBackClick = {},
            onViewPlansClick = {},
            onManageSubscriptionClick = {},
            onCancelSubscriptionClick = {}
        )
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFFF7FAF8
)
@Composable
private fun MyPlanWithoutSubscriptionPreview() {
    MaterialTheme {
        MyPlanContent(
            uiState = MyPlanUiState(
                hasSubscription = false,
                isLoading = false
            ),
            onBackClick = {},
            onViewPlansClick = {},
            onManageSubscriptionClick = {},
            onCancelSubscriptionClick = {}
        )
    }
}