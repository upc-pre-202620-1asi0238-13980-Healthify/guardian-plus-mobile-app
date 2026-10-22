package com.example.guardian_plus_mobile_app.features.profile.presentation.onboarding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.BrandGreen
import com.example.guardian_plus_mobile_app.core.designsystem.theme.FieldBorder
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextMuted
import com.example.guardian_plus_mobile_app.core.designsystem.theme.TextPrimary
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthErrorMessage
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthFieldLabel
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthLayout
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthPrimaryButton
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthSheetTitle
import com.example.guardian_plus_mobile_app.features.auth.presentation.common.AuthTextField
import com.example.guardian_plus_mobile_app.features.profile.domain.model.RelationshipType
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * First screen after the first sign-in: creates in Profile who the member is and whom they care for, with the
 * look of the sign-in screens. Only the parts the account is missing are asked.
 */
@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    userAccountId: String,
    needsUserProfile: Boolean,
    needsCareRecipient: Boolean,
    suggestedFullName: String?,
    viewModel: OnboardingViewModel = hiltViewModel(),
    onCompleted: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) { viewModel.start(needsUserProfile, needsCareRecipient, suggestedFullName) }

    LaunchedEffect(uiState.isCompleted) {
        if (uiState.isCompleted) {
            onCompleted()
            viewModel.onNavigationHandled()
        }
    }

    AuthLayout(
        modifier = modifier,
        headerTitle = stringResource(R.string.onboarding_header_title),
        headerSubtitle = stringResource(R.string.onboarding_header_subtitle)
    ) {
        AuthSheetTitle(
            title = stringResource(R.string.onboarding_title),
            subtitle = stringResource(R.string.onboarding_subtitle)
        )
        if (uiState.showUserProfile) {
            SectionTitle(stringResource(R.string.onboarding_you_section))
            AuthTextField(
                label = stringResource(R.string.onboarding_first_name_label),
                value = uiState.firstName,
                onValueChange = viewModel::onFirstNameChange,
                placeholder = stringResource(R.string.auth_full_name_placeholder).substringBefore(" ")
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthTextField(
                label = stringResource(R.string.onboarding_last_name_label),
                value = uiState.lastName,
                onValueChange = viewModel::onLastNameChange,
                placeholder = stringResource(R.string.auth_full_name_placeholder).substringAfter(" ")
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthTextField(
                label = stringResource(R.string.onboarding_phone_label),
                value = uiState.phoneNumber,
                onValueChange = viewModel::onPhoneNumberChange,
                placeholder = stringResource(R.string.onboarding_phone_placeholder),
                keyboardType = KeyboardType.Phone
            )
        }
        if (uiState.showCareRecipient) {
            SectionTitle(stringResource(R.string.onboarding_recipient_section))
            AuthTextField(
                label = stringResource(R.string.onboarding_first_name_label),
                value = uiState.careRecipientFirstName,
                onValueChange = viewModel::onCareRecipientFirstNameChange,
                placeholder = stringResource(R.string.onboarding_first_name_placeholder)
            )
            Spacer(modifier = Modifier.height(16.dp))
            AuthTextField(
                label = stringResource(R.string.onboarding_last_name_label),
                value = uiState.careRecipientLastName,
                onValueChange = viewModel::onCareRecipientLastNameChange,
                placeholder = stringResource(R.string.onboarding_last_name_placeholder),
                imeAction = ImeAction.Done
            )
            Spacer(modifier = Modifier.height(16.dp))
            BirthDateField(value = uiState.careRecipientBirthDate, onValueChange = viewModel::onCareRecipientBirthDateChange)
            Spacer(modifier = Modifier.height(16.dp))
            AuthFieldLabel(text = stringResource(R.string.onboarding_relationship_label))
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                RelationshipOption(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.onboarding_relationship_family),
                    selected = uiState.relationshipType == RelationshipType.FAMILY,
                    onClick = { viewModel.onRelationshipTypeChange(RelationshipType.FAMILY) }
                )
                RelationshipOption(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.onboarding_relationship_caregiver),
                    selected = uiState.relationshipType == RelationshipType.CAREGIVER,
                    onClick = { viewModel.onRelationshipTypeChange(RelationshipType.CAREGIVER) }
                )
            }
        }
        AuthErrorMessage(message = uiState.errorMessage)
        Spacer(modifier = Modifier.height(24.dp))
        AuthPrimaryButton(
            text = stringResource(R.string.onboarding_submit),
            isLoading = uiState.isLoading,
            onClick = { viewModel.submit(userAccountId) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun SectionTitle(text: String) {
    Spacer(modifier = Modifier.height(24.dp))
    Text(text = text, color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, letterSpacing = 1.5.sp)
    Spacer(modifier = Modifier.height(12.dp))
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirthDateField(value: LocalDate?, onValueChange: (LocalDate) -> Unit) {
    var isPickerOpen by remember { mutableStateOf(false) }
    AuthFieldLabel(text = stringResource(R.string.onboarding_birth_date_label))
    Spacer(modifier = Modifier.height(8.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(Color.White, RoundedCornerShape(12.dp))
            .border(1.dp, FieldBorder, RoundedCornerShape(12.dp))
            .clickable { isPickerOpen = true }
            .padding(horizontal = 16.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = value?.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                ?: stringResource(R.string.onboarding_birth_date_placeholder),
            color = if (value == null) TextMuted else TextPrimary,
            fontSize = 16.sp
        )
    }
    if (isPickerOpen) {
        // The picker works in UTC milliseconds at midnight
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = value?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { isPickerOpen = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let {
                        onValueChange(Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate())
                    }
                    isPickerOpen = false
                }) { Text(stringResource(R.string.onboarding_date_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { isPickerOpen = false }) { Text(stringResource(R.string.onboarding_date_cancel)) }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

@Composable
private fun RelationshipOption(modifier: Modifier = Modifier, text: String, selected: Boolean, onClick: () -> Unit) {
    OutlinedButton(
        modifier = modifier.height(48.dp),
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) BrandGreen else FieldBorder)
    ) {
        Text(text = text, color = if (selected) BrandGreen else TextPrimary, fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal)
    }
}
