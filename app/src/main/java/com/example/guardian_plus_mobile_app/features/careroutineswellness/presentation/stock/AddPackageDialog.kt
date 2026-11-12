package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.stock

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.careroutineswellness.domain.MedicationStock

/** Most units a single acquisition can add, to catch a mistyped quantity. */
const val MAX_UNITS_PER_ACQUISITION = 9_999

/** The units typed in the dialog, or null when they are not a whole number between 1 and [MAX_UNITS_PER_ACQUISITION]. */
fun parseUnitsAdded(text: String): Int? = text.trim().toIntOrNull()?.takeIf { it in 1..MAX_UNITS_PER_ACQUISITION }

/**
 * "Añadir envase": asks how many units were bought, starting from a whole package, before the platform
 * recalculates the balance and the next estimated purchase.
 */
@Composable
fun AddPackageDialog(
    modifier: Modifier = Modifier,
    stock: MedicationStock,
    onConfirm: (unitsAdded: Int) -> Unit,
    onDismiss: () -> Unit
) {
    // The text being typed is purely visual state, so it lives here and not in the ViewModel
    var units by rememberSaveable(stock.id) { mutableStateOf(stock.packageSize.toString()) }
    val unitsAdded = parseUnitsAdded(units)
    val title = listOfNotNull(stock.medicationName, stock.dosage).joinToString(" ")

    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        title = { Text(text = stringResource(R.string.stock_add_package_title), style = MaterialTheme.typography.titleLarge) },
        text = {
            Column {
                Text(
                    text = stringResource(R.string.stock_add_package_message, title),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                OutlinedTextField(
                    value = units,
                    onValueChange = { typed -> units = typed.filter(Char::isDigit).take(MAX_UNITS_PER_ACQUISITION.toString().length) },
                    label = { Text(text = stringResource(R.string.stock_add_package_units)) },
                    supportingText = {
                        Text(
                            text = if (unitsAdded == null) {
                                stringResource(R.string.stock_add_package_units_error, MAX_UNITS_PER_ACQUISITION)
                            } else {
                                stringResource(R.string.stock_add_package_units_hint, stock.packageSize)
                            }
                        )
                    },
                    isError = unitsAdded == null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
                    shape = MaterialTheme.shapes.medium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { unitsAdded?.let(onConfirm) }, enabled = unitsAdded != null) {
                Text(text = stringResource(R.string.stock_add_package_confirm), style = MaterialTheme.typography.titleSmall)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(R.string.action_cancel), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    )
}

@Preview
@Composable
private fun AddPackageDialogPreview() {
    GuardianTheme(dynamicColor = false) {
        AddPackageDialog(
            stock = MedicationStock("1", "Losartán", "50 mg", 6, 2.0, 30, 3.0, null, true),
            onConfirm = {},
            onDismiss = {}
        )
    }
}
