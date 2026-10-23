package com.example.guardian_plus_mobile_app.features.profile.presentation.terms

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
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
fun TermsScreen(
    onBackClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding =
            androidx.compose.foundation.layout.PaddingValues(
                horizontal = 20.dp,
                vertical = 14.dp
            ),
        verticalArrangement =
            Arrangement.spacedBy(18.dp)
    ) {

        item {
            TermsHeader(
                onBackClick = onBackClick
            )
        }

        item {
            Text(
                text = "ÚLTIMA ACTUALIZACIÓN: 2026",
                style =
                    MaterialTheme.typography.labelSmall,
                color =
                    MaterialTheme
                        .colorScheme.onSurfaceVariant
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
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(18.dp)
                ) {
                    TermsSection(
                        number = 1,
                        title = "Uso del servicio",
                        body =
                            "Guardian+ ofrece herramientas de apoyo para el cuidado y monitoreo remoto de personas bajo cuidado."
                    )

                    TermsSection(
                        number = 2,
                        title =
                            "Responsabilidades del usuario",
                        body =
                            "El usuario debe mantener actualizada la información registrada y utilizar la aplicación de manera responsable."
                    )

                    TermsSection(
                        number = 3,
                        title = "Uso de la información",
                        body =
                            "La información proporcionada se utiliza para ofrecer y personalizar las funcionalidades disponibles en Guardian+."
                    )

                    TermsSection(
                        number = 4,
                        title =
                            "Disponibilidad del servicio",
                        body =
                            "Algunas funcionalidades dependen de la conectividad, del dispositivo vinculado y de los servicios disponibles."
                    )

                    TermsSection(
                        number = 5,
                        title =
                            "Cambios en las condiciones",
                        body =
                            "Guardian+ podrá actualizar estas condiciones y comunicará los cambios correspondientes."
                    )
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
                        modifier =
                            Modifier.size(18.dp)
                    )

                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )

                    Text(
                        text =
                            "Consulta periódicamente esta sección para conocer la versión vigente de las condiciones de uso.",
                        style =
                            MaterialTheme
                                .typography.bodySmall
                    )
                }
            }
        }

        item {
            Button(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Volver")
            }
        }
    }
}

@Composable
private fun TermsHeader(
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
                text = "Términos y condiciones",
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Uso de Guardian+",
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme
                        .colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun TermsSection(
    number: Int,
    title: String,
    body: String
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = "$number. $title",
            style =
                MaterialTheme.typography.titleSmall
        )

        Text(
            text = body,
            style =
                MaterialTheme.typography.bodySmall,
            color =
                MaterialTheme
                    .colorScheme.onSurfaceVariant
        )
    }
}