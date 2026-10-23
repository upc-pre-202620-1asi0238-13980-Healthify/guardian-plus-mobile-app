package com.example.guardian_plus_mobile_app.features.profile.presentation.about

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R

@Composable
fun AboutScreen(
    onBackClick: () -> Unit,
    onTermsClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                horizontal = 20.dp,
                vertical = 14.dp
            ),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        item {
            AboutHeader(
                onBackClick = onBackClick
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
                    containerColor =
                        MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment =
                        Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        modifier = Modifier.size(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        color =
                            MaterialTheme
                                .colorScheme.secondaryContainer
                    ) {
                        Box(
                            contentAlignment =
                                Alignment.Center
                        ) {
                            Icon(
                                imageVector =
                                    Icons.Outlined.Shield,
                                contentDescription = null,
                                tint =
                                    MaterialTheme
                                        .colorScheme.primary,
                                modifier =
                                    Modifier.size(28.dp)
                            )
                        }
                    }

                    Text(
                        text = "Guardian+",
                        style =
                            MaterialTheme
                                .typography.titleLarge
                    )

                    Text(
                        text =
                            "Cuidado y monitoreo remoto",
                        style =
                            MaterialTheme
                                .typography.bodySmall,
                        color =
                            MaterialTheme
                                .colorScheme
                                .onSurfaceVariant
                    )

                    Surface(
                        shape =
                            RoundedCornerShape(8.dp),
                        color =
                            MaterialTheme
                                .colorScheme
                                .secondaryContainer
                    ) {
                        Text(
                            text = "Versión 1.0",
                            style =
                                MaterialTheme
                                    .typography.labelSmall,
                            color =
                                MaterialTheme
                                    .colorScheme.primary,
                            modifier =
                                Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                        )
                    }
                }
            }
        }

        item {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "SOBRE GUARDIAN+",
                    style =
                        MaterialTheme
                            .typography.labelSmall,
                    color =
                        MaterialTheme
                            .colorScheme.onSurfaceVariant
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text =
                            "Guardian+ ayuda a familiares y cuidadores a acompañar de forma remota a personas bajo cuidado mediante una experiencia centralizada y accesible.",
                        style =
                            MaterialTheme
                                .typography.bodyMedium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }

        item {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INFORMACIÓN",
                    style =
                        MaterialTheme
                            .typography.labelSmall,
                    color =
                        MaterialTheme
                            .colorScheme.onSurfaceVariant
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(
                        1.dp,
                        MaterialTheme.colorScheme.outline
                    ),
                    colors = CardDefaults.cardColors(
                        containerColor =
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    AboutInfoRow(
                        label = "Versión de la aplicación",
                        value = "1.0"
                    )

                    AboutInfoRow(
                        label = "Desarrollado por",
                        value = "Healthify"
                    )
                }
            }
        }

        item {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "INFORMACIÓN LEGAL",
                    style =
                        MaterialTheme
                            .typography.labelSmall,
                    color =
                        MaterialTheme
                            .colorScheme.onSurfaceVariant
                )

                Card(
                    onClick = onTermsClick,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
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
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment =
                            Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(40.dp),
                            shape =
                                RoundedCornerShape(10.dp),
                            color =
                                MaterialTheme
                                    .colorScheme
                                    .secondaryContainer
                        ) {
                            Box(
                                contentAlignment =
                                    Alignment.Center
                            ) {
                                Icon(
                                    imageVector =
                                        Icons.Outlined.Info,
                                    contentDescription = null,
                                    tint =
                                        MaterialTheme
                                            .colorScheme.primary,
                                    modifier =
                                        Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(
                            modifier =
                                Modifier.width(12.dp)
                        )

                        Column(
                            modifier =
                                Modifier.weight(1f)
                        ) {
                            Text(
                                text =
                                    "Términos y condiciones",
                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )

                            Text(
                                text =
                                    "Consulta las condiciones de uso de Guardian+",
                                style =
                                    MaterialTheme
                                        .typography.bodySmall,
                                color =
                                    MaterialTheme
                                        .colorScheme
                                        .onSurfaceVariant
                            )
                        }

                        Icon(
                            painter =
                                painterResource(
                                    R.drawable
                                        .ic_chevron_right
                                ),
                            contentDescription = null
                        )
                    }
                }
            }
        }

        item {
            Surface(
                color =
                    MaterialTheme
                        .colorScheme.secondaryContainer,
                shape = RoundedCornerShape(14.dp)
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
                        tint =
                            MaterialTheme
                                .colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier = Modifier.width(10.dp)
                    )

                    Text(
                        text =
                            "Gracias por utilizar Guardian+ para acompañar el cuidado de las personas que más importan.",
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
private fun AboutHeader(
    onBackClick: () -> Unit
) {
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
                    painter =
                        painterResource(
                            R.drawable.ic_chevron_left
                        ),
                    contentDescription = "Volver"
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = "Acerca de Guardian+",
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Información de la aplicación",
                style = MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AboutInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement =
            Arrangement.SpaceBetween,
        verticalAlignment =
            Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}