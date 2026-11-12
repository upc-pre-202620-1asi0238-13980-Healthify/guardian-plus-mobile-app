package com.example.guardian_plus_mobile_app.features.profile.presentation.switcher

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.profile.domain.model.CareRecipient
import java.time.LocalDate

/**
 * "Cambiar persona": one account may care for several people. Picking another one makes the whole app show that
 * person ([onSwitched] lets the shell reload the tabs); "Agregar persona" opens the form for one more.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CareRecipientSwitcherSheet(
    modifier: Modifier = Modifier,
    viewModel: CareRecipientSwitcherViewModel = hiltViewModel(),
    onSwitched: () -> Unit,
    onAddClick: () -> Unit,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.isSwitched) {
        if (uiState.isSwitched) onSwitched()
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        CareRecipientSwitcherContent(
            uiState = uiState,
            // Picking the person already shown only closes the sheet
            onSelect = { id -> if (id == uiState.currentCareRecipientId) onDismiss() else viewModel.select(id) },
            onAddClick = onAddClick,
            onRetryClick = viewModel::load
        )
    }
}

@Composable
fun CareRecipientSwitcherContent(
    modifier: Modifier = Modifier,
    uiState: CareRecipientSwitcherUiState,
    onSelect: (String) -> Unit,
    onAddClick: () -> Unit,
    onRetryClick: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 24.dp)
            .navigationBarsPadding()
    ) {
        Text(
            text = stringResource(R.string.switcher_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.switcher_subtitle),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 4.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))

        when {
            uiState.isLoading -> Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            uiState.careRecipients.isEmpty() && uiState.errorMessage != null -> OutlinedButton(
                onClick = onRetryClick,
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(text = stringResource(R.string.action_retry))
            }

            // Only the list scrolls, so "Agregar persona" stays in reach
            else -> Column(
                modifier = Modifier
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                uiState.careRecipients.forEach { careRecipient ->
                    CareRecipientRow(
                        careRecipient = careRecipient,
                        isCurrent = careRecipient.id == uiState.currentCareRecipientId,
                        isSwitching = careRecipient.id == uiState.switchingToId,
                        enabled = uiState.switchingToId == null,
                        onClick = { onSelect(careRecipient.id) }
                    )
                }
            }
        }

        uiState.errorMessage?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        OutlinedButton(
            onClick = onAddClick,
            enabled = uiState.switchingToId == null,
            modifier = Modifier
                .padding(top = 20.dp)
                .fillMaxWidth()
                .height(52.dp),
            shape = MaterialTheme.shapes.medium,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_user_plus),
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(R.string.switcher_add))
        }
    }
}

/** One person under care: initial, name and, for the one shown now, "Persona actual" and a check. */
@Composable
private fun CareRecipientRow(
    careRecipient: CareRecipient,
    isCurrent: Boolean,
    isSwitching: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = if (isCurrent) scheme.primaryContainer else scheme.surface,
        border = BorderStroke(1.dp, if (isCurrent) scheme.primary else scheme.outline)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(if (isCurrent) scheme.surface else scheme.primaryContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = careRecipient.firstName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = scheme.primary
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 12.dp)
            ) {
                Text(text = careRecipient.fullName, style = MaterialTheme.typography.titleSmall, color = scheme.onSurface)
                if (isCurrent) {
                    Text(
                        text = stringResource(R.string.switcher_current),
                        style = MaterialTheme.typography.bodySmall,
                        color = scheme.onSurfaceVariant
                    )
                }
            }
            when {
                isSwitching -> CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                isCurrent -> Icon(
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = null,
                    tint = scheme.primary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CareRecipientSwitcherContentPreview() {
    GuardianTheme(dynamicColor = false) {
        CareRecipientSwitcherContent(
            uiState = CareRecipientSwitcherUiState(
                isLoading = false,
                careRecipients = listOf(
                    CareRecipient("elena", "Elena", "Rojas", LocalDate.parse("1948-03-12")),
                    CareRecipient("jorge", "Jorge", "Rojas", LocalDate.parse("1945-07-02"))
                ),
                currentCareRecipientId = "elena"
            ),
            onSelect = {},
            onAddClick = {},
            onRetryClick = {}
        )
    }
}
