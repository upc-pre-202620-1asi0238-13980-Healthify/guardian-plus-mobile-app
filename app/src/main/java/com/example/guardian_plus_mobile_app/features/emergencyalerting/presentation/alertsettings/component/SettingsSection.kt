package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertsettings.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme

/** Titled card that groups related settings. */
@Composable
fun SettingsSection(
    modifier: Modifier = Modifier,
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
        ) {
            Column(modifier = Modifier.padding(vertical = 4.dp), content = content)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsSectionPreview() {
    GuardianTheme(dynamicColor = false) {
        SettingsSection(modifier = Modifier.padding(16.dp), title = "Pulsera") {
            Text(text = "Contenido", modifier = Modifier.padding(16.dp))
        }
    }
}
