package com.example.jawwna.datasource.localdatasoource

import android.content.Context
import com.example.jawwna.datasource.database.WeatherDAO
import com.example.jawwna.datasource.database.WeatherDatabase
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.shared_preferences_helper.WeatherResponseEntity

object LocalDataSource : ILocalDataSource {

    private lateinit var weatherDAO: WeatherDAO

    fun init(context: Context) {
        weatherDAO = WeatherDatabase.getDatabase(context).currentWeatherDao()
    }


    override suspend fun insertWeatherLocalData(currentWeather: WeatherResponseEntity) {
        weatherDAO.insertWeatherLocalData(currentWeather)
    }

}

