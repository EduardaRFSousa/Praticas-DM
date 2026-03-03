package com.example.weatherapp.ui

import com.example.weatherapp.R
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.weatherapp.model.MainViewModel
import com.example.weatherapp.model.Weather
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun MapPage (modifier: Modifier = Modifier, viewModel: MainViewModel) {
    val camPosState = rememberCameraPositionState()
    val context = LocalContext.current
    val hasLocationPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) ==
                    PackageManager.PERMISSION_GRANTED
        )
    }
    GoogleMap(
        modifier = Modifier.fillMaxSize(), onMapClick = {
            viewModel.addCity(location = it)
        },
        cameraPositionState = camPosState,
        properties = MapProperties(isMyLocationEnabled = hasLocationPermission),
        uiSettings = MapUiSettings(myLocationButtonEnabled = true)
    ) {
        val cities = viewModel.cities.collectAsStateWithLifecycle(emptyMap()).value
        val weathers = viewModel.weather.collectAsStateWithLifecycle(emptyMap()).value

        cities.values.forEach {
            if (it.location != null) {
                val weather = weathers[it.name] ?: Weather.LOADING
                LaunchedEffect(it.name) {
                    viewModel.loadWeather(it.name)
                }
                LaunchedEffect(weather) {
                    viewModel.loadBitmap(it.name)
                }

                val baseBitmap = weather.bitmap ?: BitmapFactory.decodeResource(
                    context.resources,
                    R.drawable.loading
                )

                val scaledBitmap = Bitmap.createScaledBitmap(baseBitmap, 150, 150, false)
                val iconDescriptor = BitmapDescriptorFactory.fromBitmap(scaledBitmap)

                Marker(
                    state = rememberMarkerState(position = it.location),
                    title = it.name,
                    snippet = "Temp: ${weather.temp}℃",
                    icon = iconDescriptor // Aplica o ícone maior/loading
                )
            }
        }
    }
}