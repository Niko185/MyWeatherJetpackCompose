package com.example.myweatherjetpackcompose.models

data class Weather(
    val nameCity: String,
    val time: String,
    val description: String,
    val image: String,
    val currentTemperature: String,
    val maxTemperature: String,
    val minTemperature: String,
    val hours: String
)
