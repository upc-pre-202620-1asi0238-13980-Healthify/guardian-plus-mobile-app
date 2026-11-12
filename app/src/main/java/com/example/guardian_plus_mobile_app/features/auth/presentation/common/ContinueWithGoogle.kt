package com.example.guardian_plus_mobile_app.features.auth.presentation.common

import android.content.Context
import com.example.guardian_plus_mobile_app.features.auth.application.SignInWithGoogleUseCase
import com.example.guardian_plus_mobile_app.features.auth.domain.model.valueobjects.AuthenticatedSession
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.google.GoogleIdTokenProvider
import com.example.guardian_plus_mobile_app.features.auth.infrastructure.google.GoogleSignInCancelledException
import javax.inject.Inject

/**
 * The "Continue with Google" of both the login and the sign-up: the platform signs in the Google account,
 * creating it the first time, so both screens run the same flow.
 */
class ContinueWithGoogle @Inject constructor(
    private val googleIdTokenProvider: GoogleIdTokenProvider,
    private val signInWithGoogle: SignInWithGoogleUseCase
) {
    /** [activityContext] hosts Google's account picker; it is only used during the call, never kept. */
    suspend operator fun invoke(activityContext: Context): Result<AuthenticatedSession> {
        val idToken = googleIdTokenProvider.requestIdToken(activityContext).getOrElse { return Result.failure(it) }
        return signInWithGoogle(idToken)
    }
}

/** Message to show for a failed Google sign-in; null when the user just closed the account picker. */
fun Throwable.googleSignInErrorMessage(): String? =
    if (this is GoogleSignInCancelledException) null else message ?: "No se pudo iniciar sesión con Google"
