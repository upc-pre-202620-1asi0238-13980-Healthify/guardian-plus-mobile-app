package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.vitalhistory.component

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.healthmonitoring.domain.VitalSignType
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.shortLabel

// One "Presión" chip for both halves of the blood pressure: the chart follows the systolic value
private val filterTypes = listOf(
    VitalSignType.HR,
    VitalSignType.BP_SYS,
    VitalSignType.SPO2,
    VitalSignType.TEMP,
    VitalSignType.RESP_RATE
)

/** "Ritmo · Presión · SpO₂ · Temp · Respir": which vital sign the weekly chart shows. */
@Composable
fun VitalTypeFilterRow(
    modifier: Modifier = Modifier,
    selected: VitalSignType,
    onSelect: (VitalSignType) -> Unit
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filterTypes.forEach { type ->
            val isSelected = type == selected
            Surface(
                onClick = { onSelect(type) },
                shape = MaterialTheme.shapes.medium,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.primaryContainer
            ) {
                Text(
                    text = type.shortLabel,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun VitalTypeFilterRowPreview() {
    GuardianTheme(dynamicColor = false) {
        VitalTypeFilterRow(modifier = Modifier.padding(16.dp), selected = VitalSignType.HR, onSelect = {})
    }
}
