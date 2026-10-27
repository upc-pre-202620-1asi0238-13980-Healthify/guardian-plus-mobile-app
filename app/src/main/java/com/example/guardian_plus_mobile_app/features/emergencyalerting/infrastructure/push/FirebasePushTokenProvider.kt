package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.push

import com.example.guardian_plus_mobile_app.features.emergencyalerting.domain.PushTokenProvider
import com.google.android.gms.tasks.Task
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class FirebasePushTokenProvider @Inject constructor() : PushTokenProvider {

    override suspend fun currentToken(): Result<String> = FirebaseMessaging.getInstance().token.await()

    override suspend fun deleteToken(): Result<Unit> =
        FirebaseMessaging.getInstance().deleteToken().await().map { }

    // Firebase answers with a Play Services Task; this turns it into a Result without another dependency
    private suspend fun <T> Task<T>.await(): Result<T> = suspendCancellableCoroutine { continuation ->
        addOnCompleteListener { task ->
            val result = if (task.isSuccessful) {
                Result.success(task.result)
            } else {
                Result.failure(task.exception ?: Exception("No se pudo obtener el token de notificaciones"))
            }
            continuation.resume(result)
        }
    }
}
