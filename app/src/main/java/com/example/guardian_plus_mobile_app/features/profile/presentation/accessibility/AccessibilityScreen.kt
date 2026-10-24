package com.example.guardian_plus_mobile_app.features.profile.presentation.accessibility

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppFontScale

@Composable
fun AccessibilityScreen(
    onBackClick: () -> Unit,
    viewModel: AccessibilityViewModel =
        hiltViewModel()
) {
    val uiState by
    viewModel.uiState.collectAsStateWithLifecycle()

    val snackbar =
        remember { SnackbarHostState() }

    LaunchedEffect(
        uiState.successMessage,
        uiState.errorMessage
    ) {
        val message =
            uiState.successMessage
                ?: uiState.errorMessage

        if (message != null) {
            snackbar.showSnackbar(message)
            viewModel.clearMessage()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(
                modifier =
                    Modifier.align(Alignment.Center)
            )
        } else {
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
                    AccessibilityHeader(
                        onBackClick = onBackClick
                    )
                }

                item {
                    Text(
                        text =
                            "Adapta la interfaz para que Guardian+ sea más cómoda y fácil de leer.",
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme
                                .colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Text(
                        text = "TEXTO",
                        style =
                            MaterialTheme.typography.labelSmall
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    ) {
                        Column(
                            modifier =
                                Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Tamaño del texto",
                                style =
                                    MaterialTheme
                                        .typography.titleSmall
                            )

                            Text(
                                text =
                                    "Ajusta el tamaño de los textos de la aplicación",
                                style =
                                    MaterialTheme
                                        .typography.bodySmall
                            )

                            Row(
                                modifier =
                                    Modifier.fillMaxWidth(),
                                horizontalArrangement =
                                    Arrangement.spacedBy(6.dp)
                            ) {
                                FontScaleChip(
                                    text = "Pequeño",
                                    selected =
                                        uiState.fontScale ==
                                                AppFontScale.SMALL,
                                    onClick = {
                                        viewModel.selectFontScale(
                                            AppFontScale.SMALL
                                        )
                                    },
                                    modifier =
                                        Modifier.weight(1f)
                                )

                                FontScaleChip(
                                    text = "Predeterminado",
                                    selected =
                                        uiState.fontScale ==
                                                AppFontScale.DEFAULT,
                                    onClick = {
                                        viewModel.selectFontScale(
                                            AppFontScale.DEFAULT
                                        )
                                    },
                                    modifier =
                                        Modifier.weight(1.4f)
                                )

                                FontScaleChip(
                                    text = "Grande",
                                    selected =
                                        uiState.fontScale ==
                                                AppFontScale.LARGE,
                                    onClick = {
                                        viewModel.selectFontScale(
                                            AppFontScale.LARGE
                                        )
                                    },
                                    modifier =
                                        Modifier.weight(1f)
                                )
                            }

                            Surface(
                                shape =
                                    RoundedCornerShape(12.dp),
                                color =
                                    MaterialTheme
                                        .colorScheme.surfaceVariant
                            ) {
                                Column(
                                    modifier =
                                        Modifier.padding(14.dp)
                                ) {
                                    Text(
                                        text =
                                            "EJEMPLO DE TEXTO",
                                        style =
                                            MaterialTheme
                                                .typography.labelSmall,
                                        color =
                                            MaterialTheme
                                                .colorScheme.primary
                                    )

                                    Text(
                                        text =
                                            "Así se verá el contenido en Guardian+.",
                                        style =
                                            MaterialTheme
                                                .typography.bodyMedium
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Text(
                        text = "VISUALIZACIÓN",
                        style =
                            MaterialTheme.typography.labelSmall
                    )

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    ) {
                        AccessibilitySwitchRow(
                            title = "Alto contraste",
                            subtitle =
                                "Aumenta la diferencia entre texto, fondos y controles",
                            checked =
                                uiState.highContrastEnabled,
                            onCheckedChange =
                                viewModel::setHighContrast
                        )

                        AccessibilitySwitchRow(
                            title = "Reducir movimiento",
                            subtitle =
                                "Disminuye animaciones y transiciones de la interfaz",
                            checked =
                                uiState.reduceMotionEnabled,
                            onCheckedChange =
                                viewModel::setReduceMotion
                        )
                    }
                }

                item {
                    Surface(
                        color =
                            MaterialTheme
                                .colorScheme.secondaryContainer,
                        shape =
                            RoundedCornerShape(14.dp)
                    ) {
                        Row(
                            modifier =
                                Modifier.padding(16.dp)
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
                                    "Estas preferencias cambian la presentación de la aplicación, pero no modifican la información de cuidado.",
                                style =
                                    MaterialTheme
                                        .typography.bodySmall
                            )
                        }
                    }
                }

                item {
                    Button(
                        onClick = viewModel::save,
                        enabled = !uiState.isSaving,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(
                                modifier =
                                    Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Guardar cambios")
                        }
                    }
                }

                item {
                    TextButton(
                        onClick =
                            viewModel::resetDefaults,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Restablecer valores predeterminados"
                        )
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier =
                Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun AccessibilityHeader(
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
                text = "Accesibilidad",
                style =
                    MaterialTheme.typography.titleLarge
            )

            Text(
                text = "Preferencias de visualización",
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
private fun FontScaleChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text)
        },
        modifier = modifier
    )
}

@Composable
private fun AccessibilitySwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                style =
                    MaterialTheme.typography.titleSmall
            )

            Text(
                text = subtitle,
                style =
                    MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme
                        .colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}