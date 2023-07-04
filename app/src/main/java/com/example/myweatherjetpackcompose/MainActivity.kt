package com.example.myweatherjetpackcompose

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.myweatherjetpackcompose.models.Weather
import com.example.myweatherjetpackcompose.screens.DialogSearch
import com.example.myweatherjetpackcompose.screens.MainCard
import com.example.myweatherjetpackcompose.screens.MainTabLayoutLineAndEmptyPager
import org.json.JSONObject

const val API_KEY = "99227bc267bb4ce8a9080001231402"
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val daysList = remember {
                mutableStateOf(listOf<Weather>())
            }
            val currentDay = remember {
                mutableStateOf(
                    Weather(
                        "",
                        "",
                        "",
                        "",
                        "0",
                        "0",
                        "0",
                        ""
                    )
                )
            }

            val dialogState = remember {
                mutableStateOf<Boolean>(false)
            }
            if(dialogState.value == true) {
                DialogSearch(dialogState, onSubmit = {
                    getData(it, this, daysList, currentDay)
                })
            }

            getData("London", this, daysList, currentDay)
            Image(
                painter = painterResource(id = R.drawable.image_background),
                contentDescription = "image_background",
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.8f),
                contentScale = ContentScale.Crop
            )
            Column(Modifier.fillMaxSize()) {
                MainCard(daysList, currentDay, onClickUpdateWeather = {
                    getData("London", this@MainActivity, daysList, currentDay)
                }, onClickSearchWeather = {
                    dialogState.value = true
                })
                MainTabLayoutLineAndEmptyPager(daysList, currentDay)
            }
        }
    }
}

private fun getData(city: String, context: Context, daysList: MutableState<List<Weather>>, currentDay: MutableState<Weather>) {
        val url = "https://api.weatherapi.com/v1/forecast.json?key=" +
                API_KEY +
                "&q=" +
                city +
                "&days=" +
                "3" +
                "&aqi=no&alerts=no"

        val queue = Volley.newRequestQueue(context)
        val mainRequest = StringRequest(
            Request.Method.GET,
            url,
            { response ->
                val list = getWeather(response)
                currentDay.value = list[0]
                daysList.value = list
            },
            { error -> Log.d("MyLog", "error MainRequest: $error") }
        )
        queue.add(mainRequest)
}

private fun getWeather(response: String): List<Weather> {
    if(response.isEmpty()) return listOf()
    val list = ArrayList<Weather>()
    val mainObject = JSONObject(response)
    val city = mainObject.getJSONObject("location").getString("name")
    val days = mainObject.getJSONObject("forecast").getJSONArray("forecastday")

    for(index in 0 until days.length()) {
        val itemWeather = days[index] as JSONObject
        list.add(
            Weather(
                nameCity =  city,
                time = itemWeather.getString("date"),
                currentTemperature = "",
                description = itemWeather.getJSONObject("day").getJSONObject("condition").getString("text"),
                image = itemWeather.getJSONObject("day").getJSONObject("condition").getString("icon"),
                minTemperature = itemWeather.getJSONObject("day").getString("mintemp_c"),
                maxTemperature = itemWeather.getJSONObject("day").getString("maxtemp_c"),
                hours = itemWeather.getJSONArray("hour").toString()
            )
        )
    }
    list[0] = list[0].copy(
        time = mainObject.getJSONObject("current").getString("last_updated"),
        currentTemperature = mainObject.getJSONObject("current").getString("temp_c")
    )
    return list
}