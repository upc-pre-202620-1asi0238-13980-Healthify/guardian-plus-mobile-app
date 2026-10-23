package com.example.guardian_plus_mobile_app.features.profile.presentation.language

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
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.features.profile.domain.model.AppLanguage

@Composable
fun LanguageScreen(
    onBackClick: () -> Unit,
    viewModel: LanguageViewModel = hiltViewModel()
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
                modifier = Modifier.align(
                    Alignment.Center
                )
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
                    SimpleSettingsHeader(
                        title = "Idioma",
                        subtitle =
                            "Idioma de la aplicación",
                        onBackClick = onBackClick
                    )
                }

                item {
                    Text(
                        text =
                            "Selecciona el idioma que deseas utilizar en Guardian+.",
                        style =
                            MaterialTheme.typography.bodyMedium,
                        color =
                            MaterialTheme
                                .colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Text(
                        text = "IDIOMA",
                        style =
                            MaterialTheme.typography.labelSmall,
                        color =
                            MaterialTheme
                                .colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.outline
                        )
                    ) {
                        LanguageOption(
                            title =
                                "Español (Latinoamérica)",
                            code = "es-419",
                            selected =
                                uiState.language ==
                                        AppLanguage
                                            .SPANISH_LATIN_AMERICA,
                            onClick = {
                                viewModel.selectLanguage(
                                    AppLanguage
                                        .SPANISH_LATIN_AMERICA
                                )
                            }
                        )

                        LanguageOption(
                            title =
                                "English (United States)",
                            code = "en-US",
                            selected =
                                uiState.language ==
                                        AppLanguage
                                            .ENGLISH_UNITED_STATES,
                            onClick = {
                                viewModel.selectLanguage(
                                    AppLanguage
                                        .ENGLISH_UNITED_STATES
                                )
                            }
                        )
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
                                    "El cambio de idioma se guardará como preferencia de la aplicación.",
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
                        onClick = onBackClick,
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {
                        Text("Cancelar")
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbar,
            modifier = Modifier.align(
                Alignment.BottomCenter
            )
        )
    }
}

@Composable
private fun LanguageOption(
    title: String,
    code: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(
            containerColor =
                MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment =
                Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color =
                    MaterialTheme
                        .colorScheme.secondaryContainer
            ) {
                Box(
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector =
                            Icons.Outlined.Language,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style =
                        MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = code,
                    style =
                        MaterialTheme.typography.bodySmall,
                    color =
                        MaterialTheme
                            .colorScheme.onSurfaceVariant
                )
            }

            RadioButton(
                selected = selected,
                onClick = onClick
            )
        }
    }
}

@Composable
private fun SimpleSettingsHeader(
    title: String,
    subtitle: String,
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
                text = title,
                style = MaterialTheme.typography.titleLarge
            )

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color =
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}