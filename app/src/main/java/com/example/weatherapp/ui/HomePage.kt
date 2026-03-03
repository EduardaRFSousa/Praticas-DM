package com.example.weatherapp.ui

import com.example.weatherapp.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.weatherapp.model.Forecast
import com.example.weatherapp.model.MainViewModel
import java.text.DecimalFormat

@Composable
fun ForecastItem(
    forecast: Forecast,
    onClick: (Forecast) -> Unit,
    modifier : Modifier = Modifier
){
    val format = DecimalFormat("#.0")
    val tempMin = format.format(forecast.tempMin)
    val tempMax = format.format(forecast.tempMax)

    Row (
        modifier = modifier.fillMaxWidth().padding(12.dp)
            .clickable( onClick = { onClick(forecast) }),
        verticalAlignment = Alignment.CenterVertically
    ){
        AsyncImage( // Substitui o Icon
            model = forecast.imgUrl,
            modifier = Modifier.size(70.dp),
            error = painterResource(id = R.drawable.loading),
            contentDescription = "Imagem"
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            Text(modifier = Modifier, text = forecast.weather, fontSize = 24.sp)
            Row{
                Text(modifier = Modifier, text = forecast.date, fontSize = 20.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Min: $tempMin℃", fontSize = 16.sp)
                Spacer(modifier = Modifier.size(12.dp))
                Text(modifier = Modifier, text = "Max: $tempMax℃", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun HomePage(viewModel: MainViewModel) {
    Column {
        if (viewModel.city == null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Um ícone suave de localização ou clima
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Nenhuma cidade selecionada",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Vá em 'Favoritos' ou no 'Mapa' para escolher um local.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            val cities = viewModel.cities.collectAsStateWithLifecycle(emptyMap()).value
            val city = cities[viewModel.city!!]
            val weather = viewModel.weather.collectAsStateWithLifecycle(emptyMap())
                .value[viewModel.city!!]
            val forecasts = viewModel.forecast.collectAsStateWithLifecycle(emptyMap())
                .value[viewModel.city!!]

            LaunchedEffect(viewModel.city!!) {
                viewModel.loadForecast(viewModel.city!!)
            }

            // Row principal que separa os dados (esquerda) da imagem (direita)
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Coluna com Nome, Monitoramento, Descrição e Temp
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = viewModel.city ?: "Selecione uma cidade...",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.size(8.dp))
                        if (city != null) {
                            val icon = if (city.isMonitored) Icons.Filled.Notifications
                            else Icons.Outlined.Notifications
                            Icon(
                                imageVector = icon,
                                contentDescription = "Monitorada?",
                                modifier = Modifier.size(32.dp).clickable {
                                    viewModel.update(city = city.copy(isMonitored = !city.isMonitored))
                                }
                            )
                        }
                    }

                    weather?.let {
                        Text(text = it.desc, fontSize = 22.sp)
                        Text(text = "Temp: ${it.temp}℃", fontSize = 22.sp)
                    }
                }

                // Imagem do clima atual na lateral direita
                weather?.let {
                    AsyncImage(
                        model = it.imgUrl,
                        modifier = Modifier.size(100.dp),
                        error = painterResource(id = R.drawable.loading),
                        contentDescription = "Clima atual"
                    )
                }
            }

            // Lista de previsões abaixo
            forecasts?.let { list ->
                LazyColumn {
                    items(items = list) { forecast ->
                        ForecastItem(forecast, onClick = { })
                    }
                }
            }
        }
    }
}
