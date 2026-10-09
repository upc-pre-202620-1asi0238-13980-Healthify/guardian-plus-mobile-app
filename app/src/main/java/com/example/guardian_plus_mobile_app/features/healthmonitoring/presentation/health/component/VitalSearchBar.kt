package com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.health.component

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilter
import com.example.guardian_plus_mobile_app.features.healthmonitoring.presentation.common.VitalFilterOption

/** "Todos los signos vitales ›": opens "Buscar y filtrar" and reads back what is applied. */
@Composable
fun VitalSearchBar(
    modifier: Modifier = Modifier,
    filter: VitalFilter,
    onClick: () -> Unit
) {
    val text = if (filter.isEmpty) {
        stringResource(R.string.health_all_vitals)
    } else {
        // Listed in the sheet's order, not in the order they were tapped
        VitalFilterOption.entries.filter { it in filter.options }.map { stringResource(it.labelRes) }.joinToString(" · ")
    }

    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (filter.isEmpty) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.primary)
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(if (filter.isEmpty) R.drawable.ic_search else R.drawable.ic_sliders_horizontal),
                contentDescription = null,
                tint = if (filter.isEmpty) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 10.dp)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Preview
@Composable
private fun VitalSearchBarPreview() {
    GuardianTheme(dynamicColor = false) {
        VitalSearchBar(filter = VitalFilter(), onClick = {})
    }
}

@Preview
@Composable
private fun VitalSearchBarFilteredPreview() {
    GuardianTheme(dynamicColor = false) {
        VitalSearchBar(
            filter = VitalFilter().toggle(VitalFilterOption.HEART_RATE).toggle(VitalFilterOption.DAY),
            onClick = {}
        )
    }
}
