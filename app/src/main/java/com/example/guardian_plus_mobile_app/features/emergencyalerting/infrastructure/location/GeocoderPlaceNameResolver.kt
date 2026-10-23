package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.location

import android.content.Context
import android.location.Address
import android.location.Geocoder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.roundToLong

/**
 * Resolves place names with the phone's Geocoder, which needs no API key. The alert screens re-read the
 * context every few seconds, so names are kept per ~10 m cell instead of asking the Geocoder each time.
 */
@Singleton
class GeocoderPlaceNameResolver @Inject constructor(
    @ApplicationContext private val context: Context
) : PlaceNameResolver {

    private val names = ConcurrentHashMap<Pair<Long, Long>, String>()

    override suspend fun nameOf(latitude: Double, longitude: Double): String? {
        if (!Geocoder.isPresent()) return null
        val cell = (latitude * CELLS_PER_DEGREE).roundToLong() to (longitude * CELLS_PER_DEGREE).roundToLong()
        names[cell]?.let { return it }
        val name = withContext(Dispatchers.IO) {
            runCatching {
                // The blocking call is deprecated from API 33 but still works, and the app supports API 24
                @Suppress("DEPRECATION")
                Geocoder(context, SPANISH).getFromLocation(latitude, longitude, 1)?.firstOrNull()?.toPlaceName()
            }.getOrNull()
        } ?: return null
        names[cell] = name
        return name
    }

    // "Av. Larco 345 · Miraflores": the street first, then the district
    private fun Address.toPlaceName(): String? {
        val street = listOfNotNull(thoroughfare, subThoroughfare).joinToString(" ").ifBlank { featureName }
        val district = subLocality ?: locality
        return listOfNotNull(street, district).distinct().joinToString(" · ").ifBlank { null }
    }

    private companion object {
        const val CELLS_PER_DEGREE = 10_000.0
        val SPANISH: Locale = Locale.forLanguageTag("es-PE")
    }
}
