package com.example.guardian_plus_mobile_app.features.emergencyalerting.presentation.activealerts.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.guardian_plus_mobile_app.R
import com.example.guardian_plus_mobile_app.core.designsystem.theme.GuardianTheme
import com.example.guardian_plus_mobile_app.core.designsystem.theme.noticeContainer
import com.example.guardian_plus_mobile_app.core.designsystem.theme.onNoticeContainer

/** Yellow notice that explains the escalation rule, using the timeout configured for the person under care. */
@Composable
fun EscalationNotice(modifier: Modifier = Modifier, ackTimeoutSec: Int) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.noticeContainer, MaterialTheme.shapes.medium)
            .padding(14.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onNoticeContainer,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.active_alerts_escalation_notice, ackTimeoutSec),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onNoticeContainer
        )
    }
}

@Preview
@Composable
private fun EscalationNoticePreview() {
    GuardianTheme(dynamicColor = false) {
        EscalationNotice(ackTimeoutSec = 60)
    }
}
