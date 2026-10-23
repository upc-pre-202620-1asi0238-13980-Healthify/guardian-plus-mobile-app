package com.example.guardian_plus_mobile_app.features.emergencyalerting.infrastructure.location

/** Turns the coordinates sent by the wristband into a place a family member recognizes ("Av. Larco 345 · Miraflores"). */
fun interface PlaceNameResolver {
    /** @return the place name, or null when it cannot be resolved */
    suspend fun nameOf(latitude: Double, longitude: Double): String?
}
