package com.example.jawwna.datasource.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity(tableName = "WeatherLocalData")
data class WeatherResponseEntity(
    @PrimaryKey() var cityName: String,
    @TypeConverters(WeatherDataConverter::class)
    var currentWeatherList: List<CurrentWeather>, // List of current weather data
    @TypeConverters(ForcastDailyDataConverter::class)
    var dailyForecastList: List<WeatherResponse>, // List of daily weather forecasts
    @TypeConverters(ForcastHourlyDataConverter::class)
    var hourlyForecastList: List<ForecastResponse>, // List of hourly weather forecasts
    var latitude:Double,
    var longitude: Double,
    var displayName: String? = null

)

@Entity(tableName = "FavoriteWeatherEntity")
data class FavoriteWeatherEntity(
    @PrimaryKey() var cityName: String,
    @TypeConverters(WeatherDataConverter::class)
    var currentWeatherList: List<CurrentWeather>, // List of current weather data
    @TypeConverters(ForcastDailyDataConverter::class)
    var dailyForecastList: List<WeatherResponse>, // List of daily weather forecasts
    @TypeConverters(ForcastHourlyDataConverter::class)
    var hourlyForecastList: List<ForecastResponse>, // List of hourly weather forecasts
    var latitude:Double,
    var longitude: Double,
    var displayName: String? = null

)


class ForcastDailyDataConverter {
    @TypeConverter
    fun fromWeatherList(weatherList: List<WeatherResponse>): String {
        return Gson().toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherListString: String): List<WeatherResponse> {
        val listType = object : TypeToken<List<WeatherResponse>>() {}.type
        return Gson().fromJson(weatherListString, listType)
    }
}

class  ForcastHourlyDataConverter {
    @TypeConverter
    fun fromWeatherList(weatherList: List<ForecastResponse>): String {
        return Gson().toJson(weatherList)
    }
    @TypeConverter
    fun toWeatherList(weatherListString: String): List<ForecastResponse> {
        val listType = object : TypeToken<List<ForecastResponse>>() {}.type
        return Gson().fromJson(weatherListString, listType)
    }

}


