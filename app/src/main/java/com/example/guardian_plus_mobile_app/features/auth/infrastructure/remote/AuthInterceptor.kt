package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Sends IAM's JWT with every request once there is a session; the platform validates it on its side.
 *
 * The /auth endpoints never get it: the platform rejects any invalid token it receives, so a stale session would
 * otherwise block the very login that replaces it. If the platform rejects the token (expired, or signed with keys
 * the backend no longer has), the session is dropped so the next requests go out without it.
 */
class AuthInterceptor @Inject constructor(
    private val sessionRepository: SessionRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val session = sessionRepository.currentSession()
        if (session == null || request.url.encodedPath.contains(AUTH_PATH)) {
            return chain.proceed(request)
        }
        val response = chain.proceed(
            request.newBuilder()
                .header("Authorization", "Bearer ${session.accessToken}")
                .build()
        )
        val rejectedToken = response.code == 401 &&
            response.header("WWW-Authenticate")?.contains("invalid_token") == true
        if (rejectedToken) {
            sessionRepository.clear()
        }
        return response
    }

    private companion object {
        const val AUTH_PATH = "/auth/"
    }
}
