package com.example.jawwna.datasource.localdatasource


import com.example.jawwna.datasource.localdatasoource.ILocalDataSource
import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.WeatherResponseEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class FakeLocalDataSource : ILocalDataSource {

    private val weatherData = mutableListOf<WeatherResponseEntity>()
    private val favoriteWeatherData = mutableListOf<FavoriteWeatherEntity>()
    private val alarmData = mutableListOf<AlarmEntity>()

    override suspend fun insertWeatherLocalData(currentWeather: WeatherResponseEntity) {
        weatherData.add(currentWeather)
    }

    override suspend fun getWeatherLocalData(cityName: String): WeatherResponseEntity? {
        return weatherData.find { it.cityName == cityName }
    }

    override suspend fun deleteWeatherLocalData(cityName: String) {
        weatherData.removeIf { it.cityName == cityName }
    }

    override suspend fun deleteAllWeatherLocalData() {
        weatherData.clear()
    }

    override fun getAllWeatherLocalData(): Flow<List<WeatherResponseEntity>> {
        return flowOf(weatherData)
    }

    override suspend fun insertFavoriteWeather(favoriteWeather: FavoriteWeatherEntity) {
        favoriteWeatherData.add(favoriteWeather)
    }

    override suspend fun getFavoriteWeather(cityName: String): FavoriteWeatherEntity? {
        return favoriteWeatherData.find { it.cityName == cityName }
    }

    override fun getAllFavoriteWeather(): Flow<List<FavoriteWeatherEntity>> {
        return flowOf(favoriteWeatherData)
    }

    override suspend fun deleteFavoriteWeather(favoriteWeather: FavoriteWeatherEntity) {
        favoriteWeatherData.remove(favoriteWeather)
    }

    override suspend fun deleteAllFavoriteWeather() {
        favoriteWeatherData.clear()
    }

    override suspend fun deleteFavoriteWeatherByCityName(cityName: String) {
        favoriteWeatherData.removeIf { it.cityName == cityName }
    }

    override suspend fun getAlarmByDateTime(alarmDate: String, alarmTime: String): AlarmEntity? {
        return alarmData.find { it.date == alarmDate && it.time == alarmTime }
    }

    override suspend fun deleteAlarmByDateTime(alarmDate: String, alarmTime: String) {
        alarmData.removeIf { it.date == alarmDate && it.time == alarmTime }
    }

    override suspend fun insertAlarm(alarm: AlarmEntity) {
        alarmData.add(alarm)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return flowOf(alarmData)
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        alarmData.remove(alarm)
    }

    override suspend fun changeAlarmStatus(date: String, time: String, newStatus: Boolean)
    {
        val alarm = alarmData.find { it.date == date && it.time == time }
        alarm?.let {
            it.isActive = newStatus
        }
    }

    override suspend fun getAlarmByDateTimeAndType(alarmDate: String, alarmTime: String, type: String): AlarmEntity? {
        return alarmData.find { it.date == alarmDate && it.time == alarmTime && it.type == type }
    }
}
