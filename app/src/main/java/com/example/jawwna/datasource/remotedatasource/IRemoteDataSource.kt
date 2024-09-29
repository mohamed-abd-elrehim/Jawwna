package com.example.jawwna.datasource.remotedatasource

import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.http.GET

interface IRemoteDataSource {
    //Current weather data
    suspend fun getCurrenWeatherByLatLon(lat: Double, lon: Double, apiKey: String, lang: String? = null, units: String? = null): CurrentWeather
    suspend fun getCurrenWeatherByCityName(cityName: String, apiKey: String, lang: String? = null, units: String? = null): CurrentWeather
    suspend fun getCurrenWeatherByCityAndCountry(query: String, apiKey: String, lang: String? = null, units: String? = null): CurrentWeather

    //5 day weather forecast
    suspend fun getForecastByLatLon(lat: Double, lon: Double, apiKey: String, lang: String? = null, units: String? = null):  Flow<ForecastResponse>
    suspend fun getForecastByCityName(cityName: String, apiKey: String, lang: String? = null, units: String? = null):  Flow<ForecastResponse>
    suspend fun getForecastByCityAndCountry(query: String, apiKey: String, lang: String? = null, units: String? = null):  Flow<ForecastResponse>


    //Hourly forecast data
    suspend fun getHourlyForecastByLatLon(lat: Double, lon: Double, apiKey: String, lang: String? = null, units: String? = null):  Flow<ForecastResponse>
    suspend fun getHourlyForecastByCityName(cityName: String, apiKey: String, lang: String? = null, units: String? = null):  Flow<ForecastResponse>
    suspend fun getHourlyForecastByCityAndCountry(query: String, apiKey: String, lang: String? = null, units: String? = null):  Flow<ForecastResponse>

//16-day forecast data
    suspend fun getForecastDailyByLatLon(lat: Double, lon: Double, apiKey: String, lang: String? = null, units: String? = null): Flow<WeatherResponse>
    suspend fun getForecastDailyByCityName(cityName: String, apiKey: String, lang: String? = null, units: String? = null): Flow<WeatherResponse>

    //Alerts data




}