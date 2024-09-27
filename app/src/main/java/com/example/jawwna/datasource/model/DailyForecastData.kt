package com.example.jawwna.datasource.model

data class DailyForecastData (
    val dayName: String?,
    val icon: String?,
    val description: String?,
    val tempMax:TemperatureResult,
    val tempMin: TemperatureResult


    //val tempUnit: String?
)

data class TemperatureResult(val value: Double, val unit: String)
