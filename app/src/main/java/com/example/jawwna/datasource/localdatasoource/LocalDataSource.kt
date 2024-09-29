package com.example.jawwna.datasource.localdatasoource

import android.content.Context
import com.example.jawwna.datasource.database.WeatherDAO
import com.example.jawwna.datasource.database.WeatherDatabase
import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow

object LocalDataSource : ILocalDataSource {

    private lateinit var weatherDAO: WeatherDAO

    fun init(context: Context) {
        weatherDAO = WeatherDatabase.getDatabase(context).currentWeatherDao()
    }

    override suspend fun insertWeatherLocalData(currentWeather: WeatherResponseEntity) {
        weatherDAO.insertWeatherLocalData(currentWeather)
    }
    override suspend fun getWeatherLocalData(cityName: String): WeatherResponseEntity? {
        return weatherDAO.getWeatherLocalData(cityName)
    }
    override suspend fun deleteWeatherLocalData(cityName: String) {
        weatherDAO.deleteWeatherLocalData(cityName)
    }
    override suspend fun deleteAllWeatherLocalData() {
        weatherDAO.deleteAllWeatherLocalData()
        }
    override fun getAllWeatherLocalData(): Flow<List<WeatherResponseEntity>> {
        return weatherDAO.getAllWeatherLocalData()
    }

    override suspend fun insertFavoriteWeather(favoriteWeather: FavoriteWeatherEntity) {
        weatherDAO.insertFavoriteWeather(favoriteWeather)
    }

    override suspend fun getFavoriteWeather(cityName: String): FavoriteWeatherEntity? {
      return weatherDAO.getFavoriteWeather(cityName)
    }

    override fun getAllFavoriteWeather(): Flow<List<FavoriteWeatherEntity>> {
        return weatherDAO.getAllFavoriteWeather()
    }

    override suspend fun deleteFavoriteWeather(favoriteWeather: FavoriteWeatherEntity) {
        weatherDAO.deleteFavoriteWeather(favoriteWeather)
    }

    override suspend fun deleteAllFavoriteWeather() {
        weatherDAO.deleteAllFavoriteWeather()
    }

    override suspend fun deleteFavoriteWeatherByCityName(cityName: String) {
        weatherDAO.deleteFavoriteWeatherByCityName(cityName)
    }

    override suspend fun deleteAlarmByDateTime(alarmDate: String, alarmTime: String) {
        weatherDAO.deleteAlarmByDateTime(alarmDate, alarmTime)
    }


    override suspend fun insertAlarm(alarm: AlarmEntity) {
        weatherDAO.insertAlarm(alarm)
    }

    override  fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return weatherDAO.getAllAlarms()
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        weatherDAO.deleteAlarm(alarm)
    }


}

