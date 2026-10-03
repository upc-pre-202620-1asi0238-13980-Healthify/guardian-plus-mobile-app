package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Alert
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertSourceType
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.AlertStatus
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.RecipientLevel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component.DayHeader
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component.HistoryAlertItem
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component.HistorySummaryCard
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alerthistory.component.SeverityFilterRow
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.labelRes
import java.time.Instant
import java.time.LocalDate

// How close to the end of the list the next page starts loading
private const val LOAD_MORE_THRESHOLD = 3

@Composable
fun AlertHistoryScreen(
    modifier: Modifier = Modifier,
    viewModel: AlertHistoryViewModel = hiltViewModel(),
    onAlertClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    AlertHistoryContent(
        modifier = modifier,
        uiState = uiState,
        onSeveritySelect = viewModel::selectSeverity,
        onAlertClick = onAlertClick,
        onLoadMore = viewModel::loadMore,
        onRefresh = viewModel::refresh,
        onRetryClick = viewModel::loadHistory
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertHistoryContent(
    modifier: Modifier = Modifier,
    uiState: AlertHistoryUiState,
    onSeveritySelect: (Severity?) -> Unit,
    onAlertClick: (String) -> Unit,
    onLoadMore: () -> Unit,
    onRefresh: () -> Unit,
    onRetryClick: () -> Unit
) {
    val listState = rememberLazyListState()
    LoadMoreWhenNearEnd(listState = listState, onLoadMore = onLoadMore)

    PullToRefreshBox(
        isRefreshing = uiState.isRefreshing,
        onRefresh = onRefresh,
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "filters") {
                SeverityFilterRow(selected = uiState.severityFilter, onSelect = onSeveritySelect)
            }
            uiState.summary?.let { summary ->
                item(key = "summary") { HistorySummaryCard(summary = summary) }
            }

            when {
                uiState.isLoading -> item(key = "loading") {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                uiState.errorMessage != null && uiState.alerts.isEmpty() -> item(key = "error") {
                    MessageState(
                        message = uiState.errorMessage,
                        actionLabel = stringResource(R.string.action_retry),
                        onActionClick = onRetryClick
                    )
                }

                uiState.alerts.isEmpty() -> item(key = "empty") {
                    MessageState(
                        title = stringResource(R.string.history_empty_title).takeIf { uiState.severityFilter == null },
                        message = uiState.severityFilter?.let { severity ->
                            stringResource(R.string.history_empty_filtered, stringResource(severity.labelRes()).lowercase())
                        } ?: stringResource(R.string.history_empty_message)
                    )
                }

                else -> uiState.alertsByDay.forEach { (day, alerts) ->
                    item(key = "day-$day") { DayHeader(day = day, today = uiState.today) }
                    items(alerts, key = { it.id }) { alert ->
                        HistoryAlertItem(
                            alert = alert,
                            careRecipientFirstName = uiState.careRecipientFirstName,
                            onClick = { onAlertClick(alert.id) }
                        )
                    }
                }
            }

            if (uiState.isLoadingMore) {
                item(key = "loading-more") {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    }
                }
            }
        }
    }
}

/** Asks for the next page when the last visible row gets close to the end of the list. */
@Composable
private fun LoadMoreWhenNearEnd(listState: LazyListState, onLoadMore: () -> Unit) {
    val nearEnd by remember {
        derivedStateOf {
            val lastVisible = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            lastVisible >= listState.layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }
    LaunchedEffect(nearEnd) {
        if (nearEnd) onLoadMore()
    }
}

@Composable
private fun MessageState(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp, horizontal = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        title?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 4.dp)
        )
        actionLabel?.let {
            OutlinedButton(onClick = onActionClick, modifier = Modifier.padding(top = 12.dp), shape = MaterialTheme.shapes.medium) {
                Text(text = it)
            }
        }
    }
}

private val previewToday: LocalDate = LocalDate.of(2026, 10, 3)

private fun previewAlert(id: String, source: AlertSourceType, severity: Severity, status: AlertStatus, at: String, ackSec: Long?) =
    Alert(
        id = id,
        careRecipientProfileId = "elena",
        sourceType = source,
        severity = severity,
        status = status,
        currentRecipientLevel = RecipientLevel.PRIMARY,
        triggeredAt = Instant.parse(at),
        acknowledgedAt = ackSec?.let { Instant.parse(at).plusSeconds(it) }
    )

@Preview(showBackground = true, heightDp = 900)
@Composable
private fun AlertHistoryContentPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertHistoryContent(
            uiState = AlertHistoryUiState(
                careRecipientFirstName = "Elena",
                today = previewToday,
                summary = HistorySummary(totalAlerts = 12, averageResponseSec = 48, escalatedCount = 2),
                alerts = listOf(
                    previewAlert("1", AlertSourceType.VITAL_SIGN_ANOMALY, Severity.HIGH, AlertStatus.RESOLVED, "2026-10-03T19:23:00Z", 60),
                    previewAlert("2", AlertSourceType.FALL_DETECTED, Severity.CRITICAL, AlertStatus.DISMISSED, "2026-10-03T16:05:00Z", null),
                    previewAlert("3", AlertSourceType.SOS_TRIGGERED, Severity.CRITICAL, AlertStatus.RESOLVED, "2026-10-02T22:58:00Z", 32)
                )
            ),
            onSeveritySelect = {},
            onAlertClick = {},
            onLoadMore = {},
            onRefresh = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertHistoryContentLoadingPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertHistoryContent(
            uiState = AlertHistoryUiState(isLoading = true),
            onSeveritySelect = {},
            onAlertClick = {},
            onLoadMore = {},
            onRefresh = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertHistoryContentErrorPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertHistoryContent(
            uiState = AlertHistoryUiState(errorMessage = "No se pudo conectar con el servidor. Revisa tu conexión."),
            onSeveritySelect = {},
            onAlertClick = {},
            onLoadMore = {},
            onRefresh = {},
            onRetryClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertHistoryContentEmptyPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertHistoryContent(
            uiState = AlertHistoryUiState(severityFilter = Severity.HIGH),
            onSeveritySelect = {},
            onAlertClick = {},
            onLoadMore = {},
            onRefresh = {},
            onRetryClick = {}
        )
    }
}
