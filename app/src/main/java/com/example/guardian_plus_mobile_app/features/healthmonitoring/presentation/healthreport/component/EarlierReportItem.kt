package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.HealthReport
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.ReadingBadge
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.generatedAtText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.labelRes
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.periodText
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.healthreport.previewHealthReport

/** A row of "Reportes anteriores": its period, kind and whether the person stayed stable. */
@Composable
fun EarlierReportItem(modifier: Modifier = Modifier, report: HealthReport, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = report.periodText(), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurface)
                Text(
                    text = stringResource(report.reportType.labelRes) + " · " + report.generatedAtText(),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            ReadingBadge(withinRange = report.clinicallyStable)
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun EarlierReportItemPreview() {
    GuardianTheme(dynamicColor = false) {
        EarlierReportItem(report = previewHealthReport(), onClick = {})
    }
}
