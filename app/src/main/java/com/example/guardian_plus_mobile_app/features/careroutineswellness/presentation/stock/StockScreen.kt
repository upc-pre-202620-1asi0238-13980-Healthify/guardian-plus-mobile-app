package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBadge
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineBanner
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineCard
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineEmptyState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineErrorState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineInfoLine
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineLoadingState
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineMessageEffect
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutinePrimaryButton
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineProgressBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineSectionTitle
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTone
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.RoutineTopBar
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.colors
import com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common.formatDayOfMonth
import java.time.LocalDate
import kotlin.math.ceil

/** "Stock de medicinas": doses left of each medication and how long they last. */
@Composable
fun StockScreen(
    modifier: Modifier = Modifier,
    viewModel: StockViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    RoutineMessageEffect(uiState.message, snackbarHostState, viewModel::onMessageShown)

    Box(modifier = modifier) {
        StockContent(uiState = uiState, onBackClick = onBackClick, onRetryClick = viewModel::load, onAddPackage = viewModel::addPackage)
        SnackbarHost(hostState = snackbarHostState, modifier = Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
fun StockContent(
    modifier: Modifier = Modifier,
    uiState: StockUiState,
    onBackClick: () -> Unit,
    onRetryClick: () -> Unit,
    onAddPackage: (MedicationStock) -> Unit
) {
    Column(modifier = modifier.fillMaxSize()) {
        RoutineTopBar(
            title = stringResource(R.string.routines_category_stock),
            subtitle = stringResource(R.string.stock_subtitle),
            onBackClick = onBackClick
        )
        Box(modifier = Modifier.weight(1f)) {
            when {
                !uiState.isLoaded && uiState.errorMessage == null -> RoutineLoadingState()
                !uiState.isLoaded -> RoutineErrorState(message = uiState.errorMessage.orEmpty(), onRetryClick = onRetryClick)
                uiState.stocks.isEmpty() -> RoutineEmptyState(
                    iconRes = R.drawable.ic_layers,
                    title = stringResource(R.string.routines_stock_none),
                    message = stringResource(R.string.stock_empty_message)
                )

                else -> LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    uiState.lowStocks.firstOrNull()?.let { lowest ->
                        item(key = "restock") {
                            val days = lowest.wholeDaysOfSupply()
                            RoutineBanner(
                                iconRes = R.drawable.ic_layers,
                                title = stringResource(R.string.stock_restock_title),
                                message = pluralStringResource(R.plurals.stock_restock_message, days, lowest.medicationName, days),
                                tone = RoutineTone.ORANGE
                            )
                        }
                    }
                    item(key = "title") { RoutineSectionTitle(text = stringResource(R.string.stock_medications_title)) }
                    items(uiState.stocks, key = { it.id }) { stock ->
                        StockCard(
                            stock = stock,
                            isRestocking = uiState.restockingId == stock.id,
                            onAddPackageClick = { onAddPackage(stock) }
                        )
                    }
                    item(key = "how-title") { RoutineSectionTitle(text = stringResource(R.string.stock_how_title)) }
                    item(key = "how") { RoutineInfoLine(iconRes = R.drawable.ic_clipboard_check, text = stringResource(R.string.stock_how_message)) }
                }
            }
        }
    }
}

/** One medication: name, balance, a bar against a full package and when it runs out. */
@Composable
private fun StockCard(stock: MedicationStock, isRestocking: Boolean, onAddPackageClick: () -> Unit) {
    val low = stock.restockRecommended
    val (visualContainer, visualContent) = (if (low) RoutineTone.YELLOW else RoutineTone.MINT).colors()
    val days = stock.wholeDaysOfSupply()
    val depletion = stock.estimatedDepletionDate
    val title = listOfNotNull(stock.medicationName, stock.dosage).joinToString(" ")
    val dailyDoses = stock.dailyConsumption.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() }

    RoutineCard {
        Row(verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(visualContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(painter = painterResource(R.drawable.ic_pill), contentDescription = null, tint = visualContent, modifier = Modifier.size(26.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                    if (low) {
                        RoutineBadge(text = stringResource(R.string.stock_low), tone = RoutineTone.ORANGE)
                    } else {
                        RoutineBadge(text = stringResource(R.string.stock_available))
                    }
                }
                Text(
                    text = pluralStringResource(R.plurals.stock_balance, stock.remainingDoses, dailyDoses, stock.remainingDoses),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                RoutineProgressBar(
                    modifier = Modifier.padding(vertical = 10.dp),
                    percentage = stock.packagePercentage,
                    progress = if (low) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = when {
                        low -> pluralStringResource(R.plurals.stock_runs_out_in, days, days)
                        depletion != null -> stringResource(R.string.stock_next_purchase, formatDayOfMonth(depletion))
                        else -> pluralStringResource(R.plurals.stock_runs_out_in, days, days)
                    },
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (low) {
            Spacer(modifier = Modifier.height(12.dp))
            RoutinePrimaryButton(
                text = stringResource(R.string.stock_add_package),
                isLoading = isRestocking,
                onClick = onAddPackageClick
            )
        }
    }
}

// "para 3 días": a partial day still counts as a day of medication at hand
private fun MedicationStock.wholeDaysOfSupply(): Int = ceil(remainingDaysOfSupply).toInt()

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun StockContentPreview() {
    GuardianTheme(dynamicColor = false) {
        StockContent(
            uiState = StockUiState(
                isLoaded = true,
                stocks = listOf(
                    MedicationStock("1", "Losartán", "50 mg", 6, 2.0, 30, 3.0, LocalDate.parse("2026-10-11"), true),
                    MedicationStock("2", "Metformina", "850 mg", 38, 2.0, 50, 19.0, LocalDate.parse("2026-10-27"), false)
                )
            ),
            onBackClick = {},
            onRetryClick = {},
            onAddPackage = {}
        )
    }
}
