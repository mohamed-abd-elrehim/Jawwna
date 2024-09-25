package com.example.jawwna.datasource.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


@Entity(tableName = "WeatherLocalData")
data class WeatherResponseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @TypeConverters(WeatherDataConverter::class)
    var currentWeatherList: List<CurrentWeather>, // List of current weather data
    @TypeConverters(ForcastDailyDataConverter::class)
    var dailyForecastList: List<WeatherResponse>, // List of daily weather forecasts
    @TypeConverters(ForcastHourlyDataConverter::class)
    var hourlyForecastList: List<ForecastResponse>, // List of hourly weather forecasts
    var cityName: String // Name of the city for which the weather is stored
)

@Entity(tableName = "FavoriteWeatherEntity")
data class FavoriteWeatherEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @TypeConverters(WeatherDataConverter::class)
    var currentWeatherList: List<CurrentWeather>, // List of current weather data
    @TypeConverters(ForcastDailyDataConverter::class)
    var dailyForecastList: List<WeatherResponse>, // List of daily weather forecasts
    @TypeConverters(ForcastHourlyDataConverter::class)
    var hourlyForecastList: List<ForecastResponse>, // List of hourly weather forecasts
    var cityName: String // Name of the city for which the weather is stored
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


