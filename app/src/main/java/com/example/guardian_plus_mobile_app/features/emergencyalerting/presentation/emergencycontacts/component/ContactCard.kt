package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.emergencycontacts.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.dataLabel
import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.EmergencyContact
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.common.StatusChip

/**
 * One contact of the escalation chain with its place in it. Arrows reorder the chain (US16) and
 * the trash button removes the contact; both are disabled while a change is being saved.
 */
@Composable
fun ContactCard(
    modifier: Modifier = Modifier,
    contact: EmergencyContact,
    position: Int,
    isFirst: Boolean,
    isLast: Boolean,
    canRemove: Boolean,
    enabled: Boolean,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onRemove: () -> Unit
) {
    val isPrimary = position == 0
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        if (isPrimary) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = contact.displayName.split(" ").filter { it.isNotBlank() }.take(2)
                        .joinToString("") { it.first().uppercase() },
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isPrimary) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                StatusChip(
                    text = if (isPrimary) {
                        stringResource(R.string.contacts_role_primary)
                    } else {
                        stringResource(R.string.contacts_role_secondary, position)
                    },
                    container = if (isPrimary) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
                    content = if (isPrimary) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = contact.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(top = 6.dp)
                )
                Text(
                    text = contact.relationship,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = contact.phoneNumber,
                    style = MaterialTheme.typography.dataLabel,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column {
                ContactIconButton(
                    iconRes = R.drawable.ic_arrow_up,
                    description = stringResource(R.string.contacts_move_up, contact.displayName),
                    enabled = enabled && !isFirst,
                    onClick = onMoveUp
                )
                ContactIconButton(
                    iconRes = R.drawable.ic_arrow_down,
                    description = stringResource(R.string.contacts_move_down, contact.displayName),
                    enabled = enabled && !isLast,
                    onClick = onMoveDown
                )
            }
            ContactIconButton(
                iconRes = R.drawable.ic_trash_2,
                description = stringResource(R.string.contacts_remove, contact.displayName),
                enabled = enabled && canRemove,
                tint = MaterialTheme.colorScheme.error,
                onClick = onRemove
            )
        }
    }
}

@Composable
private fun ContactIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    description: String,
    enabled: Boolean,
    tint: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    // 48 dp touch target, as the style guide asks for every control
    IconButton(onClick = onClick, enabled = enabled, modifier = modifier.size(48.dp)) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = description,
            tint = if (enabled) tint else MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(20.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactCardPreview() {
    GuardianTheme(dynamicColor = false) {
        ContactCard(
            modifier = Modifier.padding(16.dp),
            contact = EmergencyContact("c1", "elena", "maria", "María Rojas", "Hija", "+51987654321", 1, true),
            position = 0,
            isFirst = true,
            isLast = false,
            canRemove = true,
            enabled = true,
            onMoveUp = {},
            onMoveDown = {},
            onRemove = {}
        )
    }
}
