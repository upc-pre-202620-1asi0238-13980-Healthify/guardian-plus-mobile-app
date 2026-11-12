package com.example.guardian_plus_mobile_app.features.auth.infrastructure.google

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.guardian_plus_mobile_app.BuildConfig
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import javax.inject.Inject

/** The user closed Google's account picker; not an error worth showing. */
class GoogleSignInCancelledException : Exception()

/**
 * Asks Google, through Credential Manager, for an ID token of the account the user picks. The token's audience is
 * the platform's Web client, so the platform can verify it.
 */
class GoogleIdTokenProvider @Inject constructor() {

    /** [activityContext] must be an Activity: Credential Manager shows the account picker on top of it. */
    suspend fun requestIdToken(activityContext: Context): Result<String> {
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(GetSignInWithGoogleOption.Builder(BuildConfig.GOOGLE_WEB_CLIENT_ID).build())
            .build()
        return try {
            val credential = CredentialManager.create(activityContext).getCredential(activityContext, request).credential
            if (credential is CustomCredential && credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                Result.success(GoogleIdTokenCredential.createFrom(credential.data).idToken)
            } else {
                Result.failure(Exception(UNKNOWN_CREDENTIAL))
            }
        } catch (_: GetCredentialCancellationException) {
            Result.failure(GoogleSignInCancelledException())
        } catch (_: NoCredentialException) {
            Result.failure(Exception("No hay una cuenta de Google en este dispositivo"))
        } catch (_: GoogleIdTokenParsingException) {
            Result.failure(Exception(UNKNOWN_CREDENTIAL))
        } catch (e: GetCredentialException) {
            Result.failure(Exception("No se pudo iniciar sesión con Google: ${e.message}"))
        }
    }

    private companion object {
        const val UNKNOWN_CREDENTIAL = "Google devolvió una credencial que la app no reconoce"
    }
}
