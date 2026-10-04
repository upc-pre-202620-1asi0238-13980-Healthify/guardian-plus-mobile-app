package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.Severity

/** Container and content colors of each severity: red, orange and yellow, like a traffic light. */
data class SeverityColors(val container: Color, val content: Color)

@Composable
fun Severity.colors(): SeverityColors = when (this) {
    Severity.CRITICAL -> SeverityColors(
        MaterialTheme.colorScheme.errorContainer,
        MaterialTheme.colorScheme.onErrorContainer
    )
    Severity.HIGH -> SeverityColors(
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.tertiary
    )
    Severity.MEDIUM -> SeverityColors(
        MaterialTheme.colorScheme.noticeContainer,
        MaterialTheme.colorScheme.onNoticeContainer
    )
}

@Composable
fun SeverityBadge(modifier: Modifier = Modifier, severity: Severity) {
    val colors = severity.colors()
    Text(
        text = stringResource(severity.labelRes()),
        style = MaterialTheme.typography.labelMedium,
        color = colors.content,
        modifier = modifier
            .background(colors.container, MaterialTheme.shapes.small)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Preview
@Composable
private fun SeverityBadgePreview() {
    GuardianTheme(dynamicColor = false) {
        SeverityBadge(severity = Severity.HIGH)
    }
}
