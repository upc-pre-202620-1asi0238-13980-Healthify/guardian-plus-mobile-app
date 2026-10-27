package com.example.guardian_plus_mobile_app.features.mobilitygeofencing.presentation.location

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon

@Composable
fun MapViewComposable(
    modifier: Modifier = Modifier,
    latitude: Double,
    longitude: Double,
    isWithinSafeZone: Boolean,
    safeZoneRadius: Double = 100.0
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
            val centerPoint = GeoPoint(latitude, longitude)
            mapView.controller.setCenter(centerPoint)

            val safeZonePolygon = Polygon().apply {
                points = Polygon.pointsAsCircle(centerPoint, safeZoneRadius)
                fillColor = if (isWithinSafeZone) 0x334CAF50 else 0x33F44336
                strokeColor = if (isWithinSafeZone) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
                strokeWidth = 3f
            }
            mapView.overlays.add(safeZonePolygon)

            val marker = Marker(mapView).apply {
                position = centerPoint
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = if (isWithinSafeZone) "Ciudadano en Zona Segura" else "¡Alerta! Fuera de Perímetro"
            }
            mapView.overlays.add(marker)
            mapView.invalidate()
        }
    )
}