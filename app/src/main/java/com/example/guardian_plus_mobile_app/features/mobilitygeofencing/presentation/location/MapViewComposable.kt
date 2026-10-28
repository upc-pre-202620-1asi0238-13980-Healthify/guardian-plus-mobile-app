package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.guardian_plus_mobile_app.features.mobilitygeofencing.domain.model.SafeZone
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

/**
 * Map centered on the person's last location, with the active safe zone drawn where it really is (its own center
 * and radius), green while the person is inside and red once they left it.
 */
@Composable
fun MapViewComposable(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    isWithinSafeZone: Boolean,
    safeZone: SafeZone?
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            Configuration.getInstance().load(context, context.getSharedPreferences("osm_prefs", 0))
            MapView(context).apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                controller.setZoom(16.5)
            }
        },
        update = { mapView ->
            mapView.overlays.clear()
            val personPoint = GeoPoint(latitude, longitude)
            mapView.controller.setCenter(personPoint)

            if (safeZone != null) {
                val safeZonePolygon = Polygon().apply {
                    points = Polygon.pointsAsCircle(GeoPoint(safeZone.latitude, safeZone.longitude), safeZone.radiusInMeters)
                    fillColor = if (isWithinSafeZone) 0x334CAF50 else 0x33F44336
                    strokeColor = if (isWithinSafeZone) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
                    strokeWidth = 3f
                    title = safeZone.name
                }
                mapView.overlays.add(safeZonePolygon)
            }

            val marker = Marker(mapView).apply {
                position = personPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = when {
                    safeZone == null -> "Última ubicación"
                    isWithinSafeZone -> "Dentro de ${safeZone.name}"
                    else -> "¡Alerta! Fuera de ${safeZone.name}"
                }
            }
            mapView.overlays.add(marker)
            mapView.invalidate()
        }
    )
}
