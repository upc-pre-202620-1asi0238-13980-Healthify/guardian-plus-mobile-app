package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.PhoneNumber

// Same limits as the platform's AddEmergencyContactResource
private const val MAX_NAME_LENGTH = 100
private const val MAX_RELATIONSHIP_LENGTH = 50
private const val DEFAULT_COUNTRY_CODE = "+51"

/** Bottom sheet with the new-contact form (US16). Fields are checked here with the platform's rules before sending. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactSheet(
    modifier: Modifier = Modifier,
    careRecipientFirstName: String,
    isSaving: Boolean,
    onSave: (displayName: String, relationship: String, phoneNumber: String) -> Unit,
    onDismiss: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        shape = MaterialTheme.shapes.large,
        containerColor = MaterialTheme.colorScheme.surface,
        modifier = modifier
    ) {
        AddContactForm(
            careRecipientFirstName = careRecipientFirstName,
            isSaving = isSaving,
            onSave = onSave
        )
    }
}

@Composable
fun AddContactForm(
    modifier: Modifier = Modifier,
    careRecipientFirstName: String,
    isSaving: Boolean,
    onSave: (displayName: String, relationship: String, phoneNumber: String) -> Unit
) {
    // What the user is typing is purely visual state, so it lives here and not in the ViewModel
    var name by rememberSaveable { mutableStateOf("") }
    var relationship by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf(DEFAULT_COUNTRY_CODE) }
    // Errors only show after the first attempt, so an empty form does not start in red
    var showErrors by rememberSaveable { mutableStateOf(false) }

    val nameValid = name.isNotBlank() && name.trim().length <= MAX_NAME_LENGTH
    val relationshipValid = relationship.isNotBlank() && relationship.trim().length <= MAX_RELATIONSHIP_LENGTH
    val phoneValid = PhoneNumber.isValid(phone.trim())

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .navigationBarsPadding()
            .imePadding()
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = stringResource(R.string.contacts_add_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = stringResource(R.string.contacts_add_subtitle, careRecipientFirstName),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it.take(MAX_NAME_LENGTH) },
            label = { Text(text = stringResource(R.string.contacts_field_name)) },
            isError = showErrors && !nameValid,
            supportingText = if (showErrors && !nameValid) {
                { Text(text = stringResource(R.string.contacts_error_name)) }
            } else {
                null
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = relationship,
            onValueChange = { relationship = it.take(MAX_RELATIONSHIP_LENGTH) },
            label = { Text(text = stringResource(R.string.contacts_field_relationship)) },
            placeholder = { Text(text = stringResource(R.string.contacts_field_relationship_hint)) },
            isError = showErrors && !relationshipValid,
            supportingText = if (showErrors && !relationshipValid) {
                { Text(text = stringResource(R.string.contacts_error_relationship)) }
            } else {
                null
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences, imeAction = ImeAction.Next),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = phone,
            onValueChange = { input -> phone = input.filter { it == '+' || it.isDigit() } },
            label = { Text(text = stringResource(R.string.contacts_field_phone)) },
            isError = showErrors && !phoneValid,
            supportingText = if (showErrors && !phoneValid) {
                { Text(text = stringResource(R.string.contacts_error_phone)) }
            } else {
                null
            },
            singleLine = true,
            shape = MaterialTheme.shapes.medium,
            textStyle = MaterialTheme.typography.bodyLarge,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone, imeAction = ImeAction.Done),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(4.dp))
        Button(
            onClick = {
                showErrors = true
                if (nameValid && relationshipValid && phoneValid) onSave(name, relationship, phone)
            },
            enabled = !isSaving,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(painter = painterResource(R.drawable.ic_user_plus), contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(R.string.contacts_save))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AddContactFormPreview() {
    GuardianTheme(dynamicColor = false) {
        AddContactForm(careRecipientFirstName = "Elena", isSaving = false, onSave = { _, _, _ -> })
    }
}
