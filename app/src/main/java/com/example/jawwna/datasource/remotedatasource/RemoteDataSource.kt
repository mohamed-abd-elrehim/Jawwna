package com.example.jawwna.datasource.remotedatasource

import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.network.WeatherApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object  RemoteDataSource: IRemoteDataSource {

    private const val BASE_URL = "https://api.openweathermap.org/"
    private const val BASE_URL_HOURLY = "https://pro.openweathermap.org/"
    private var currentBaseUrl: String = BASE_URL
    private lateinit var retrofit: Retrofit
    private lateinit var weatherApiService: WeatherApiService


    init {
        initializeRetrofit()
    }

    private fun initializeRetrofit() {
        retrofit = Retrofit.Builder()
            .baseUrl(currentBaseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            weatherApiService = retrofit.create(WeatherApiService::class.java)
    }




    private fun setBaseUrl(baseUrl: String) {
        currentBaseUrl = baseUrl
        initializeRetrofit()
    }

    fun useFirstBaseUrl() {
        setBaseUrl(BASE_URL)
    }

    fun useSecondBaseUrl() {
        setBaseUrl(BASE_URL_HOURLY)
    }


    //Current weather data
    // Fetch weather data by latitude and longitude with optional lang and units
    override suspend fun getCurrenWeatherByLatLon(lat: Double, lon: Double, apiKey: String, lang: String?, units: String?): CurrentWeather {
        useFirstBaseUrl()
        return weatherApiService.getWeatherByLatLon(lat, lon, apiKey, lang, units)
    }

    // Fetch weather data by city name with optional lang and units
    override suspend fun getCurrenWeatherByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return weatherApiService.getWeatherByCityName(cityName, apiKey, lang, units)
    }

    // Fetch weather data by city and country code with optional lang and units
    override suspend fun getCurrenWeatherByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return weatherApiService.getWeatherByCityAndCountry(query, apiKey, lang, units)
    }

    //5 day weather forecast
    // Fetch 5-day/3-hour forecast data by latitude and longitude
    override suspend fun getForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return weatherApiService.getForecastByLatLon(lat, lon, apiKey, lang, units)
    }

    // Fetch 5-day/3-hour forecast data by city name
    override suspend fun getForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return weatherApiService.getForecastByCityName(cityName, apiKey, lang, units)
    }

    // Fetch 5-day/3-hour forecast data by city and country code
    override suspend fun getForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return weatherApiService.getForecastByCityAndCountry(query, apiKey, lang, units)
    }

    //Hourly forecast data
    // Fetch hourly forecast data by latitude and longitude
    override suspend fun getHourlyForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        useSecondBaseUrl()
        return weatherApiService.getHourlyForecastByLatLon(lat, lon, apiKey, lang, units)
    }

    // Fetch hourly forecast data by city name
    override suspend fun getHourlyForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return weatherApiService.getHourlyForecastByCityName(cityName, apiKey, lang, units)
    }

    // Fetch hourly forecast data by city and country code
    override suspend fun getHourlyForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return weatherApiService.getHourlyForecastByCityAndCountry(query, apiKey, lang, units)
    }


}