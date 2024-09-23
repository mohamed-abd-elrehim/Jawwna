package com.example.jawwna.datasource.network

import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiService {
    //Current weather data
    // Fetch weather data by latitude and longitude with optional lang and units
    @GET("data/2.5/weather")
    suspend fun getWeatherByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): CurrentWeather

    // Fetch weather data by city name with optional lang and units
    @GET("data/2.5/weather")
    suspend fun getWeatherByCityName(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): CurrentWeather

    // Fetch weather data by city and country code with optional lang and units
    @GET("data/2.5/weather")
    suspend fun getWeatherByCityAndCountry(
        @Query("q") query: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): CurrentWeather

    // Fetch weather data by city, state, and country code with optional lang and units
    @GET("data/2.5/weather")
    suspend fun getWeatherByCityStateAndCountry(
        @Query("q") query: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): CurrentWeather

    //Call hourly forecast data
    // Fetch hourly forecast data by latitude and longitude
    @GET("data/2.5/forecast/hourly")
    suspend fun getHourlyForecastByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): ForecastResponse

    // Fetch hourly forecast data by city name
    @GET("data/2.5/forecast/hourly")
    suspend fun getHourlyForecastByCityName(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): ForecastResponse

    // Fetch hourly forecast data by city and country code
    @GET("data/2.5/forecast/hourly")
    suspend fun getHourlyForecastByCityAndCountry(
        @Query("q") query: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null,
        @Query("units") units: String? = null
    ): ForecastResponse

    // Fetch hourly forecast data by city, state, and country code
    @GET("data/2.5/forecast/hourly")
    suspend fun getHourlyForecastByCityStateAndCountry(
        @Query("q") query: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null,
        @Query("units") units: String? = null
    ): ForecastResponse


    //5 day weather forecast

    // Fetch 5-day/3-hour forecast data by latitude and longitude
    @GET("data/2.5/forecast")
    suspend fun getForecastByLatLon(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): ForecastResponse

    // Fetch 5-day/3-hour forecast data by city name
    @GET("data/2.5/forecast")
    suspend fun getForecastByCityName(
        @Query("q") cityName: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): ForecastResponse

    // Fetch 5-day/3-hour forecast data by city and country code
    @GET("data/2.5/forecast")
    suspend fun getForecastByCityAndCountry(
        @Query("q") query: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): ForecastResponse

    // Fetch 5-day/3-hour forecast data by city, state, and country code
    @GET("data/2.5/forecast")
    suspend fun getForecastByCityStateAndCountry(
        @Query("q") query: String,
        @Query("appid") apiKey: String,
        @Query("lang") lang: String? = null, // Optional language
        @Query("units") units: String? = null // Optional units
    ): ForecastResponse

}
