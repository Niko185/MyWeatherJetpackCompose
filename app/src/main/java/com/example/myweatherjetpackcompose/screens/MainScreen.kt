package com.example.myweatherjetpackcompose.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myweatherjetpackcompose.R
import com.example.myweatherjetpackcompose.models.Weather
import com.example.myweatherjetpackcompose.ui.theme.DarkPurple
import com.google.accompanist.pager.*
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject


@Composable
fun MainCard(daysList: MutableState<List<Weather>>, weather: MutableState<Weather>, onClickUpdateWeather: () -> Unit, onClickSearchWeather: () -> Unit) {
    Column(
        modifier = Modifier
            .padding(
                top = 4.dp,
                start = 4.dp,
                end = 4.dp
                )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(264.dp)
                .alpha(0.4f),
            backgroundColor = Color.White,
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = weather.value.time,
                        style = TextStyle(fontSize = 16.sp)
                        )
                    AsyncImage(
                        model = "https:${weather.value.image}",
                        contentDescription = "image_api",
                        modifier = Modifier
                            .size(54.dp)
                        )
                }
                Column(
                    modifier = Modifier
                        .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = weather.value.nameCity,
                        style = TextStyle(fontSize = 34.sp)
                    )
                    Text(
                        text = if(weather.value.currentTemperature.isNotEmpty()) {
                            weather.value.currentTemperature.toDouble().toInt().toString()+"°C"
                        } else "${weather.value.minTemperature.toDouble().toInt()}°C / ${weather.value.maxTemperature.toDouble().toInt()}°C",
                        style = TextStyle(fontSize = 34.sp)
                    )
                    Text(
                        text = weather.value.description,
                        style = TextStyle(fontSize = 20.sp)
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom

                ) {
                    IconButton(onClick = {
                        onClickSearchWeather.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_search),
                            contentDescription = "icon_search"
                        )
                    }

                    Text("${weather.value.minTemperature.toDouble().toInt()}°C / ${weather.value.maxTemperature.toDouble().toInt()}°C", Modifier.padding(bottom = 16.dp))

                    IconButton(onClick = {
                        onClickUpdateWeather.invoke()
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.icon_update),
                            contentDescription = "icon_update"
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun MainTabLayoutLineAndEmptyPager(daysList: MutableState<List<Weather>>, currentDay: MutableState<Weather>) {
    val stateSwitchPager = rememberPagerState()
    val tabIndexCurrentPage = stateSwitchPager.currentPage
    val startAnimateCoroutineScope = rememberCoroutineScope()
    val tabTitles = listOf("TO DAY", "FORECAST")

        Column() {
            TabRow( // Line with Tabs
                selectedTabIndex = tabIndexCurrentPage,
                indicator = { tabPosition ->
                    TabRowDefaults.Indicator(
                        Modifier.pagerTabIndicatorOffset(stateSwitchPager, tabPosition)
                    )
                },
                backgroundColor = Color.White,
                contentColor = DarkPurple,
                modifier = Modifier
                    .alpha(0.7f)
                    .padding(
                        top = 4.dp,
                        start = 4.dp,
                        end = 4.dp,
                        bottom = 1.dp
                    )
                    .clip(RoundedCornerShape(4.dp)),
            ) {
                tabTitles.forEachIndexed { index, value ->
                    Tab(
                        selected = false,
                        onClick = {
                            startAnimateCoroutineScope.launch {
                                stateSwitchPager.animateScrollToPage(index)
                            }
                        },
                        text = {
                            Text(text = value)
                        }
                    )
                }
            }
            HorizontalPager(
                count = tabTitles.size,
                state = stateSwitchPager,
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.Top
            ) { index ->
                val list = when (index) { // 0 or 1
                    0 -> getCurrentWeatherByHours(currentDay.value.hours)
                    1 -> daysList.value
                    else -> daysList.value
                }
                MainListLazyColumn(listWeather = list, currentDay = currentDay)
            }
        }
    }
@Composable
fun MainListLazyColumn(listWeather: List<Weather>, currentDay: MutableState<Weather>){
    val listToShow = try{ listWeather.subList(1, listWeather.size) } catch (e:Exception){ listWeather }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        itemsIndexed(
            listToShow
        ) { _, value ->
            itemForLazyColumn(value, currentDay)
        }
    }
}

@Composable
fun itemForLazyColumn(weather: Weather, currentDay: MutableState<Weather>) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(74.dp)
            .alpha(0.7f)
            .padding(
                top = 2.dp,
                start = 4.dp,
                end = 4.dp,
            )
            .clickable {
                if (weather.hours.isEmpty()) return@clickable
                currentDay.value = weather
            },
        shape = RoundedCornerShape(4.dp)

    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(
                    text = weather.time,
                    modifier = Modifier
                        .padding(8.dp),
                    color = DarkPurple)
                Text(
                    text = weather.description,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
                    color = DarkPurple)
            }
            Text(
                text = "${weather.currentTemperature}".ifEmpty {
                    "${weather.minTemperature.toDouble().toInt()}°C / ${weather.maxTemperature.toDouble().toInt()}°C" },
                style = TextStyle(fontSize = 20.sp),
                color = DarkPurple)
            AsyncImage(
                model = "https:${weather.image}",
                contentDescription = "image_api",
                modifier = Modifier
                    .size(54.dp)
            )
        }
    }
}

@Composable
fun DialogSearch(dialogState: MutableState<Boolean>, onSubmit: (String) -> Unit) {
    val dialogText = remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
            dialogState.value = false
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(dialogText.value)
                    dialogState.value = false
                }) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    dialogState.value = false
                }) {
                Text(text = "CANCEL")
            }
        },
        title = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(text = "Input city name:")
                TextField(value = dialogText.value, onValueChange = {
                    dialogText.value = it
                })
            }
        }
    )
}

private fun getCurrentWeatherByHours(hours: String): List<Weather> {
    if(hours.isEmpty()) return listOf()
    val hoursArray = JSONArray(hours)
    val list = ArrayList<Weather>()

    for(index in 0 until hoursArray.length()) {
        val item = hoursArray[index] as JSONObject
        list.add(
            Weather(
                nameCity = "",
                time = item.getString("time"),
                currentTemperature = item.getString("temp_c").toDouble().toInt().toString() + "°C",
                description = item.getJSONObject("condition").getString("text"),
                image = item.getJSONObject("condition").getString("icon"),
                minTemperature = "",
                maxTemperature = "",
                hours = ""
            )
        )
    }
    return list
}
