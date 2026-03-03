package com.example.weatherapp.ui

import android.app.Activity
import com.example.weatherapp.R
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weatherapp.model.MainViewModel
import com.example.weatherapp.model.City
import com.example.weatherapp.model.Weather
import com.example.weatherapp.ui.nav.Route

@Composable
fun CityItem(
    city: City,
    weather: Weather,
    onClick: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val desc = if (weather == Weather.LOADING) "Carregando clima..." else weather.desc
    val monitorIcon = if (city.isMonitored)
        androidx.compose.material.icons.Icons.Filled.Notifications
    else
        androidx.compose.material.icons.Icons.Outlined.Notifications

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = weather.imgUrl,
            modifier = Modifier.size(75.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem do clima"
        )

        Spacer(modifier = Modifier.size(12.dp))

        // Coluna central: Nome em cima, Descrição embaixo
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = city.name,
                fontSize = 24.sp,
                lineHeight = 28.sp
            )
            Text(
                text = desc,
                fontSize = 16.sp,
                color = androidx.compose.ui.graphics.Color.Gray
            )
        }

        // Ícone de monitoramento (apenas visual na lista, sem clique conforme Passo 6)
        Icon(
            imageVector = monitorIcon,
            contentDescription = "Status de Monitoramento",
            modifier = Modifier.size(28.dp).padding(horizontal = 4.dp),
            tint = if (city.isMonitored) androidx.compose.ui.graphics.Color.Unspecified else androidx.compose.ui.graphics.Color.LightGray
        )

        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Remover cidade")
        }
    }
}

@Preview
@Composable
fun ListPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel
) {
    val cityMap = viewModel.cities.collectAsStateWithLifecycle(emptyMap()).value
    val cityList = cityMap.values.toList().sortedBy { it.name }
    val weatherMap = viewModel.weather.collectAsStateWithLifecycle(emptyMap()).value
    val activity = LocalContext.current as Activity // Para os Toasts

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        items(items = cityList, key = { it.name }) { city ->
            LaunchedEffect(city.name) {
                viewModel.loadWeather(city.name)
            }
            val weather = weatherMap[city.name]?:Weather.LOADING;
            CityItem(city = city, weather = weather, onClose = {
                viewModel.remove(city)
            }, onClick = {
                viewModel.city = city.name
                viewModel.page = Route.Home
            })
        }
    }
}