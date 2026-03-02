package com.example.weatherapp.ui

import android.content.pm.PackageManager
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
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

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

            }
        }
    }
}