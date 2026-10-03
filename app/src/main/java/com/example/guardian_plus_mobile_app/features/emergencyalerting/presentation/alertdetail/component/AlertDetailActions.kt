package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.alertdetail.AlertDetailAction

/** Which buttons the current state allows; every flag maps to a command the backend accepts right now. */
data class AvailableActions(
    val canAcknowledge: Boolean = false,
    val canClaim: Boolean = false,
    val canComplete: Boolean = false,
    val canStabilize: Boolean = false,
    val canClose: Boolean = false,
    val canCall: Boolean = false,
    val canCallEmergency: Boolean = false,
    val canViewHealth: Boolean = false
)

@Composable
fun AlertDetailActions(
    modifier: Modifier = Modifier,
    actions: AvailableActions,
    runningAction: AlertDetailAction?,
    careRecipientFirstName: String,
    onAcknowledgeClick: () -> Unit,
    onClaimClick: () -> Unit,
    onCompleteClick: () -> Unit,
    onStabilizeClick: () -> Unit,
    onCloseClick: () -> Unit,
    onCallClick: () -> Unit,
    onCallEmergencyClick: () -> Unit,
    onViewHealthClick: () -> Unit
) {
    val busy = runningAction != null
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        if (actions.canAcknowledge) {
            PrimaryAction(
                text = stringResource(R.string.action_acknowledge_alert),
                iconRes = R.drawable.ic_check,
                running = runningAction == AlertDetailAction.ACKNOWLEDGE,
                enabled = !busy,
                onClick = onAcknowledgeClick
            )
        }
        if (actions.canComplete) {
            PrimaryAction(
                text = stringResource(R.string.action_arrived),
                iconRes = R.drawable.ic_map_pin,
                running = runningAction == AlertDetailAction.COMPLETE,
                enabled = !busy,
                onClick = onCompleteClick
            )
        }
        if (actions.canClose && !actions.canStabilize) {
            PrimaryAction(
                text = stringResource(R.string.action_close_incident),
                iconRes = R.drawable.ic_check,
                running = runningAction == AlertDetailAction.CLOSE,
                enabled = !busy,
                onClick = onCloseClick
            )
        }
        if (actions.canClaim) {
            SecondaryAction(
                text = stringResource(R.string.action_on_my_way),
                iconRes = R.drawable.ic_map_pin,
                running = runningAction == AlertDetailAction.CLAIM,
                enabled = !busy,
                onClick = onClaimClick
            )
        }
        if (actions.canStabilize) {
            SecondaryAction(
                text = stringResource(R.string.action_stabilize),
                iconRes = R.drawable.ic_activity,
                running = runningAction == AlertDetailAction.STABILIZE,
                enabled = !busy,
                onClick = onStabilizeClick
            )
            SecondaryAction(
                text = stringResource(R.string.action_close_incident),
                iconRes = R.drawable.ic_check,
                running = runningAction == AlertDetailAction.CLOSE,
                enabled = !busy,
                onClick = onCloseClick
            )
        }
        if (actions.canCall) {
            SecondaryAction(
                text = stringResource(R.string.action_call_person, careRecipientFirstName),
                iconRes = R.drawable.ic_phone,
                running = false,
                enabled = true,
                onClick = onCallClick
            )
        }
        if (actions.canCallEmergency) {
            Button(
                onClick = onCallEmergencyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = MaterialTheme.shapes.medium,
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.4f)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                )
            ) {
                ButtonContent(text = stringResource(R.string.action_call_emergency), iconRes = R.drawable.ic_siren, running = false)
            }
        }
        if (actions.canViewHealth) {
            SecondaryAction(
                text = stringResource(R.string.action_view_health),
                iconRes = R.drawable.ic_activity,
                running = false,
                enabled = true,
                onClick = onViewHealthClick
            )
        }
    }
}

@Composable
private fun PrimaryAction(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconRes: Int,
    running: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        ButtonContent(text = text, iconRes = iconRes, running = running)
    }
}

@Composable
private fun SecondaryAction(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes iconRes: Int,
    running: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary)
    ) {
        ButtonContent(text = text, iconRes = iconRes, running = running)
    }
}

@Composable
private fun ButtonContent(text: String, @DrawableRes iconRes: Int, running: Boolean) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (running) {
            CircularProgressIndicator(
                modifier = Modifier.size(18.dp),
                strokeWidth = 2.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Icon(painter = painterResource(iconRes), contentDescription = null, modifier = Modifier.size(18.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text, maxLines = 1)
    }
}

@Preview(showBackground = true)
@Composable
private fun AlertDetailActionsPreview() {
    GuardianTheme(dynamicColor = false) {
        AlertDetailActions(
            modifier = Modifier.padding(16.dp),
            actions = AvailableActions(canAcknowledge = true, canClaim = true, canCall = true, canCallEmergency = true),
            runningAction = null,
            careRecipientFirstName = "Elena",
            onAcknowledgeClick = {},
            onClaimClick = {},
            onCompleteClick = {},
            onStabilizeClick = {},
            onCloseClick = {},
            onCallClick = {},
            onCallEmergencyClick = {},
            onViewHealthClick = {}
        )
    }
}
