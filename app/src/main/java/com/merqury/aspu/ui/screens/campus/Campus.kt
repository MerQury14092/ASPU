package com.merqury.aspu.ui.screens.campus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import com.merqury.aspu.ui.TitleHeader
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.MapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint

@Composable
fun CampusScreen(header: MutableState<@Composable () -> Unit>) {
    header.value = {
        TitleHeader(title = "Cumpussy общежития")
    }
    val cameraState = CameraState(CameraProperty())
    // add node
    OpenStreetMap(
        properties = MapProperties(
            isFlingEnable = false,
            isAnimating = false,
            minZoomLevel = 1.0
        ),
        cameraState = cameraState
    ) {
        // Главный корпус
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(45.001817, 41.132393))
        )
        // общага 1
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(45.000517, 41.126859))
        )
        // корпус 2
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(45.000415, 41.131333))
        )
        // корпус 3 - спф
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(45.002263, 41.121873))
        )
        // корпус 4 - фтеид
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(45.003697, 41.122763))
        )
        // корпус 5 - ебд
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(45.003372, 41.121388))
        )
        // фок
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(45.006374, 41.128629))
        )
        // истфак
        Marker(
            state = rememberMarkerState(geoPoint = GeoPoint(44.989082, 41.126904))
        )
    }
}