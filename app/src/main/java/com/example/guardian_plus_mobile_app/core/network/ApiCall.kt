package com.example.guardian_plus_mobile_app.core.network

import com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.remote.errorMessage
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException
import retrofit2.Response

/**
 * Runs a request and turns every possible outcome into a Result with a message the user understands.
 * [onNotFound] lets a lookup treat 404 as an expected empty answer instead of an error, and [onError] lets
 * a caller turn a status it expects (such as a 422 business rule) into its own exception.
 */
suspend fun <T, R> apiCall(
    request: suspend () -> Response<T>,
    onNotFound: (() -> R)? = null,
    onError: ((code: Int, message: String) -> Exception)? = null,
    map: (T) -> R
): Result<R> {
    return try {
        val response = request()
        val body = response.body()
        when {
            response.code() == 404 && onNotFound != null -> Result.success(onNotFound())
            !response.isSuccessful -> {
                val message = response.errorMessage()
                Result.failure(onError?.invoke(response.code(), message) ?: Exception(message))
            }
            body == null -> Result.failure(Exception("El servidor no devolvió datos"))
            else -> Result.success(map(body))
        }
    } catch (_: IOException) {
        Result.failure(Exception("No se pudo conectar con el servidor. Revisa tu conexión."))
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        // An unknown enum value, a missing field or a malformed date in the response
        Result.failure(Exception("El servidor devolvió datos que la app no reconoce: ${e.message}"))
    }
}
