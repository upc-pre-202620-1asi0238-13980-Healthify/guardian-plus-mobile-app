package com.example.guardian_plus_mobile_app.features.auth.infrastructure.remote

import com.example.guardian_plus_mobile_app.features.auth.domain.repositories.SessionRepository
import javax.inject.Inject
import okhttp3.Interceptor
import okhttp3.Response

/** Sends IAM's JWT with every request once there is a session; the platform validates it on its side. */
class AuthInterceptor @Inject constructor(
    private val sessionRepository: SessionRepository
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val session = sessionRepository.currentSession() ?: return chain.proceed(chain.request())
        val request = chain.request().newBuilder()
            .header("Authorization", "Bearer ${session.accessToken}")
            .build()
        return chain.proceed(request)
    }
}
