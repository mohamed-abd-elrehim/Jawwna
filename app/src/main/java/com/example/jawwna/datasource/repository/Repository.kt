package com.example.jawwna.datasource.repository

import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.remotedatasource.IRemoteDataSource

class Repository private constructor(private val remoteDataSource: IRepository) : IRepository  {
    // Implement the repository methods here
    // Implement the RemoteDataSource methods here
    override suspend fun getCurrenWeatherByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return remoteDataSource.getCurrenWeatherByLatLon(lat, lon, apiKey, lang, units)
    }

    override suspend fun getCurrenWeatherByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return remoteDataSource.getCurrenWeatherByCityName(cityName, apiKey, lang, units)
    }

    override suspend fun getCurrenWeatherByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return remoteDataSource.getCurrenWeatherByCityAndCountry(query, apiKey, lang, units)
    }

    override suspend fun getForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return remoteDataSource.getForecastByLatLon(lat, lon, apiKey, lang, units)
    }

    override suspend fun getForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return remoteDataSource.getForecastByCityName(cityName, apiKey, lang, units)
    }

    override suspend fun getForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return remoteDataSource.getForecastByCityAndCountry(query, apiKey, lang, units)
    }

    override suspend fun getHourlyForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return remoteDataSource.getHourlyForecastByLatLon(lat, lon, apiKey, lang, units)
    }

    override suspend fun getHourlyForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return remoteDataSource.getHourlyForecastByCityName(cityName, apiKey, lang, units)
    }

    override suspend fun getHourlyForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return remoteDataSource.getHourlyForecastByCityAndCountry(query, apiKey, lang, units)
    }


}