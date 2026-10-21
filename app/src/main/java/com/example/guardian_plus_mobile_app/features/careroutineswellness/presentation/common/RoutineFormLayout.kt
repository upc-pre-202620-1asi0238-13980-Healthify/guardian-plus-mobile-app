package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Scrollable body of a form with its save button always visible at the bottom. */
@Composable
fun RoutineFormBody(
    modifier: Modifier = Modifier,
    saveLabel: String,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier
        .fillMaxSize()
        .imePadding()) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            content = content
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            RoutinePrimaryButton(text = saveLabel, isLoading = isSaving, onClick = onSaveClick)
        }
    }
}

/** Full-width green pill button of the prototype, with progress while the platform answers. */
@Composable
fun RoutinePrimaryButton(modifier: Modifier = Modifier, text: String, isLoading: Boolean = false, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = !isLoading,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp, color = MaterialTheme.colorScheme.onPrimary)
        } else {
            Text(text = text)
        }
    }
}

/** Titled card grouping the fields of one part of a form ("Medicamento", "Horario"…). */
@Composable
fun RoutineFormSection(modifier: Modifier = Modifier, title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        title?.let { RoutineSectionTitle(text = it) }
        RoutineCard {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp), content = content)
        }
    }
}
