package com.example.guardian_plus_mobile_app.features.careroutineswellness.presentation.common

import androidx.annotation.StringRes
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

/**
 * One-off message shown in a snackbar, the app's version of the prototype's toast: a confirmation written
 * by the app ("Dosis registrada como administrada") or the platform's own reason for rejecting an action.
 */
sealed interface RoutineMessage {
    data class Text(val text: String) : RoutineMessage
    data class Resource(@param:StringRes val resId: Int, val args: List<Any> = emptyList()) : RoutineMessage
}

/** Shows [message] once and tells the ViewModel it was shown. */
@Composable
fun RoutineMessageEffect(message: RoutineMessage?, snackbarHostState: SnackbarHostState, onShown: () -> Unit) {
    val context = LocalContext.current
    LaunchedEffect(message) {
        val text = when (message) {
            null -> return@LaunchedEffect
            is RoutineMessage.Text -> message.text
            is RoutineMessage.Resource -> context.getString(message.resId, *message.args.toTypedArray())
        }
        onShown()
        snackbarHostState.showSnackbar(text)
    }
}

/** Key a form sets on the screen below it, so that screen confirms what was just saved. */
const val ROUTINE_RESULT_MESSAGE_KEY = "routine_result_message"
