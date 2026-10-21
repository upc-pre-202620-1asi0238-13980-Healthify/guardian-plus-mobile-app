package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Card of switches and selects; the rows bring their own 16 dp side padding, as in the alert settings. */
@Composable
fun RoutineSettingsCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(vertical = 6.dp), content = content)
    }
}

/** Side padding that lines a select or a note up with the switch rows of a [RoutineSettingsCard]. */
val SettingsRowPadding: Modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
