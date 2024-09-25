package com.example.jawwna.datasource.localdatasoource

import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.shared_preferences_helper.WeatherResponseEntity

interface ILocalDataSource {
     suspend fun  insertWeatherLocalData(currentWeather: WeatherResponseEntity)

}