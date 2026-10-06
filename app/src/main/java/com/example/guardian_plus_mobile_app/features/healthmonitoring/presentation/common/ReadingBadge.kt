package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip

/** "Normal" (mint) or "En observación" (yellow); the long form reads "En rango normal". */
@Composable
fun ReadingBadge(modifier: Modifier = Modifier, withinRange: Boolean, long: Boolean = false) {
    val scheme = MaterialTheme.colorScheme
    val text = when {
        !withinRange -> stringResource(R.string.reading_observation)
        long -> stringResource(R.string.reading_in_range)
        else -> stringResource(R.string.reading_normal)
    }
    StatusChip(
        modifier = modifier,
        text = text,
        container = if (withinRange) scheme.primaryContainer else scheme.noticeContainer,
        content = if (withinRange) scheme.onPrimaryContainer else scheme.onNoticeContainer
    )
}

@Preview
@Composable
private fun ReadingBadgePreview() {
    GuardianTheme(dynamicColor = false) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            ReadingBadge(withinRange = true)
            ReadingBadge(withinRange = true, long = true)
            ReadingBadge(withinRange = false)
        }
    }
}
