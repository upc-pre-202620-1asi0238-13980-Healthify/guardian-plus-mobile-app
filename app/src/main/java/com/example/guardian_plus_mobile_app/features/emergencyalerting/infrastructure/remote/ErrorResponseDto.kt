package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote

import com.google.gson.Gson
import retrofit2.Response

/** Error body returned by the platform (ErrorResource): a code and a localized message. */
data class ErrorResponseDto(
    val code: String?,
    val message: String?
)

/** Reads the backend message of a failed response, so the user sees why the action was rejected. */
fun Response<*>.errorMessage(): String {
    val backendMessage = try {
        errorBody()?.string()?.let { Gson().fromJson(it, ErrorResponseDto::class.java)?.message }
    } catch (_: Exception) {
        null
    }
    return backendMessage ?: "El servidor respondió con el código ${code()}"
}
