package com.example.jawwna.datasource.repository

import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.model.City
import com.example.jawwna.datasource.model.Clouds
import com.example.jawwna.datasource.model.Coord
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.Main
import com.example.jawwna.datasource.model.Rain
import com.example.jawwna.datasource.model.Temp
import com.example.jawwna.datasource.model.WeatherCondition
import com.example.jawwna.datasource.model.WeatherList
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.model.Wind
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.helper.TemperatureUnits
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRepository( var fake: MutableList<FavoriteWeatherEntity>) : IRepository {




    // Sample WeatherCondition
    val weatherCondition = WeatherCondition(
        id = 800,
        main = "Clear",
        description = "clear sky",
        icon = "01d"
    )

    // Sample Coord
    val coord = Coord(
        lon = 31.2357, // Longitude for Cairo, Egypt
        lat = 30.0444  // Latitude for Cairo, Egypt
    )

    // Sample Main weather data
    val mainWeather = Main(
        temp = 295.0, // Temperature in Kelvin
        temp_min = 293.0,
        temp_max = 297.0,
        pressure = 1013,
        humidity = 56
    )

    // Sample Wind data
    val wind = Wind(
        speed = 3.5 // Wind speed in m/s
    )

    // Sample Clouds data
    val clouds = Clouds(
        all = 0 // Cloudiness percentage
    )

    // Sample Rain data
    val rain = Rain(
        `1h` = null,
        `3h` = 0.0 // No rain in the last 3 hours
    )

    // Sample CurrentWeather
    val fakeCurrentWeather = CurrentWeather(
        id = 360630, // Cairo's city ID
        name = "Cairo",
        coord = coord,
        weather = listOf(weatherCondition),
        main = mainWeather,
        wind = wind,
        clouds = clouds,
        rain = rain,
        dt_txt = "2024-09-30 12:00:00"
    )

    // Sample WeatherResponse
    val weatherResponse = WeatherResponse(
        city = City(name = "Cairo", coord = coord),
        cod = "200",
        message = 0.0,
        cnt = 1,
        list = listOf(
            WeatherList(
                temp = Temp(300.0, 290.0, 310.0, 295.0, 298.0, 293.0),
                pressure = 1012,
                humidity = 50,
                weather = listOf(weatherCondition),
                speed = 2.5,
                clouds = 10,
                rain = null,
                dt = System.currentTimeMillis() / 1000
            )
        )
    )

    // Sample ForecastResponse
    val fakeForecastResponse = ForecastResponse(
        list = listOf(fakeCurrentWeather),
        city = City(name = "Cairo", coord = coord)
    )

    // Sample WeatherResponseEntity for testing
    val fakeWeatherResponseEntity = WeatherResponseEntity(
        cityName = "Cairo",
        currentWeatherList = listOf(fakeCurrentWeather),
        dailyForecastList = listOf(weatherResponse),
        hourlyForecastList = listOf(fakeForecastResponse),
        latitude = 30.0444,
        longitude = 31.2357,
        displayName = "Cairo, Egypt"
    )

    // Sample FavoriteWeatherEntity for testing
    val fakeFavoriteWeather = FavoriteWeatherEntity(
        cityName = "Cairo",
        currentWeatherList = listOf(fakeCurrentWeather),
        dailyForecastList = listOf(weatherResponse),
        hourlyForecastList = listOf(fakeForecastResponse),
        latitude = 30.0444,
        longitude = 31.2357,
        displayName = "Cairo, Egypt"
    )

    var alarms = mutableListOf<AlarmEntity>()

    // Method to insert an alarm
    override suspend fun insertAlarm(alarm: AlarmEntity) {
        alarms.add(alarm) // Add alarm to the list

    }

    // Method to delete an alarm
    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        alarms.remove(alarm) // Remove alarm from the list
    }

    override suspend fun insertFavoriteWeather(weatherEntity: FavoriteWeatherEntity) {
        fake.add(weatherEntity)
    }


    // Implement the method to retrieve a favorite weather by some identifier
    override suspend fun getFavoriteWeather(cityName: String): FavoriteWeatherEntity? {
        return fake.find { it.cityName == cityName }
    }



    val data = mutableListOf<WeatherResponseEntity>() // List to store weather data

    override fun getAllWeatherLocalData(): Flow<List<WeatherResponseEntity>> {
        return flow { emit(data) }
    }

    // Method to insert local weather data (not implemented for fake repo)
    override suspend fun insertWeatherLocalData(currentWeather: WeatherResponseEntity) {
        data.add(currentWeather) // Add weather data to the list

    }
    var  tempUnit: String = "Celsius"
    var  windUnit: String?= "km/h"

    override fun setOldTemperatureUnit(unit: String) {
        tempUnit= unit
    }

    override suspend fun getHourlyForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        // Return fake hourly forecast data
        return flow { emit(fakeForecastResponse) }
    }

    override suspend fun getForecastDailyByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<WeatherResponse> {
        // Return fake daily forecast data
        return flow { emit(weatherResponse) }
    }


    override fun setOldWindSpeedUnit(unit: String) {
        windUnit= unit
    }

    override fun execute(preferencesLocationEum: PreferencesLocationEum) {
        TODO("Not yet implemented")
    }

    override fun getLanguageCode(): String {
        TODO("Not yet implemented")
    }

    // Method to simulate getting current weather by latitude and longitude
    override suspend fun getCurrenWeatherByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return fakeCurrentWeather
    }

    // Method to simulate getting current weather by city name
    override suspend fun getCurrenWeatherByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return fakeCurrentWeather
    }

    // Method to get local weather data
    override suspend fun getWeatherLocalData(cityName: String): WeatherResponseEntity? {
        return fakeWeatherResponseEntity
    }

    // Method to get all favorite weather
    override fun getAllFavoriteWeather(): Flow<List<FavoriteWeatherEntity>> {
        return flow { emit(listOf(fakeFavoriteWeather)) }
    }

    override suspend fun deleteFavoriteWeather(favoriteWeather: FavoriteWeatherEntity) {
        fake.remove(favoriteWeather)
    }
    override fun getWindSpeedUnit(): String? {
        return windUnit

    }
    override fun getOldWindSpeedUnit(): String? {
        return  windUnit
    }
    override suspend fun getAlarmByDateTime(alarmDate: String, alarmTime: String): AlarmEntity? {
        val alarm = alarms.find { it.date == alarmDate && it.time == alarmTime }
        return alarm

    }

    override suspend fun getCurrenWeatherByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        TODO("Not yet implemented")
    }

    // Method to simulate getting forecast by latitude and longitude
    override suspend fun getForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        return flow { emit(fakeForecastResponse) }
    }

    override suspend fun getForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }



    override suspend fun getHourlyForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }

    override suspend fun getHourlyForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<ForecastResponse> {
        TODO("Not yet implemented")
    }



    override suspend fun getForecastDailyByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): Flow<WeatherResponse> {
        TODO("Not yet implemented")
    }



    override suspend fun deleteWeatherLocalData(cityName: String) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteAllWeatherLocalData() {
        TODO("Not yet implemented")
    }





    override suspend fun deleteAllFavoriteWeather() {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFavoriteWeatherByCityName(cityName: String) {
        TODO("Not yet implemented")
    }



    override suspend fun deleteAlarmByDateTime(alarmDate: String, alarmTime: String) {
        TODO("Not yet implemented")
    }



    // Method to get all alarms
    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return flow { emit(alarms) }
    }


    override suspend fun changeAlarmStatus(date: String, time: String, newStatus: Boolean) {
        TODO("Not yet implemented")
    }

    override suspend fun getAlarmByDateTimeAndType(
        alarmDate: String,
        alarmTime: String,
        type: String
    ): AlarmEntity? {
        TODO("Not yet implemented")
    }

    override fun saveGetLocationMode(mode: String) {
        TODO("Not yet implemented")
    }

    override fun getGetLocationMode(): String? {
        TODO("Not yet implemented")
    }

    override fun clearGetLocationMode() {
        TODO("Not yet implemented")
    }

    private var tempUnit2: String = TemperatureUnits.metric.toString()



    override fun saveTemperatureUnit(unit: String) {
        tempUnit2= unit
    }

    override fun getTemperatureUnit(): String? {
        return tempUnit2
    }

    override fun getOldTemperatureUnit(): String? {
        return  tempUnit2
    }

    override fun clearTemperatureUnit() {
        TODO("Not yet implemented")
    }



    override fun saveWindSpeedUnit(unit: String) {
        TODO("Not yet implemented")
    }



    override fun clearWindSpeedUnit() {
        TODO("Not yet implemented")
    }

    override fun saveLanguage(language: String) {
        TODO("Not yet implemented")
    }

    override fun getLanguage(): String? {
        TODO("Not yet implemented")
    }

    override fun clearLanguage() {
        TODO("Not yet implemented")
    }

    override fun saveTheme(theme: String) {
        TODO("Not yet implemented")
    }

    override fun getTheme(): String? {
        TODO("Not yet implemented")
    }

    override fun clearTheme() {
        TODO("Not yet implemented")
    }

    override fun saveNotifications(status: String) {
        TODO("Not yet implemented")
    }

    override fun getNotifications(): String? {
        TODO("Not yet implemented")
    }

    override fun clearNotifications() {
        TODO("Not yet implemented")
    }

    override fun clearAllSettings() {
        TODO("Not yet implemented")
    }

    override fun resetSettings() {
        TODO("Not yet implemented")
    }

    override fun saveLocationName(name: String) {
        TODO("Not yet implemented")
    }

    override fun getLocationName(): String? {
        TODO("Not yet implemented")
    }

    override fun clearLocationName() {
        TODO("Not yet implemented")
    }

    override fun saveLocationLatitude(latitude: Double) {
        TODO("Not yet implemented")
    }

    override fun getLocationLatitude(): Double? {
        TODO("Not yet implemented")
    }

    override fun clearLocationLatitude() {
        TODO("Not yet implemented")
    }

    override fun saveLocationLongitude(longitude: Double) {
        TODO("Not yet implemented")
    }

    override fun getLocationLongitude(): Double? {
        TODO("Not yet implemented")
    }

    override fun clearLocationLongitude() {
        TODO("Not yet implemented")
    }

    override fun clearAllLocation() {
        TODO("Not yet implemented")
    }

    override fun isLocationSaved(): Boolean {
        TODO("Not yet implemented")
    }

    override fun searchPlace(query: String): Flow<LatLng?> {
        TODO("Not yet implemented")
    }

    override fun getCountryNameFromLatLong(latitude: Double, longitude: Double): String? {
        TODO("Not yet implemented")
    }

    override fun isNetworkAvailable(): Boolean {
        TODO("Not yet implemented")
    }

    // Other methods can be added or modified as needed
}
