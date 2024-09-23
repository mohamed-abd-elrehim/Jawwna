package com.example.jawwna.datasource.repository

import android.app.Application
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.remotedatasource.IRemoteDataSource
import com.example.jawwna.datasource.remotedatasource.RemoteDataSource

class Repository private constructor() : IRepository  {

    companion object {
        @Volatile
        private var INSTANCE: Repository? = null

        fun getRepository(): Repository {
            return INSTANCE ?: synchronized(this) {
                Repository().also {
                    INSTANCE = it
                }
            }
        }
    }


    // Implement the repository methods here
    // Implement the RemoteDataSource methods here
    override suspend fun getCurrenWeatherByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return RemoteDataSource.getCurrenWeatherByLatLon(lat, lon, apiKey, lang, units)
    }

    override suspend fun getCurrenWeatherByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return RemoteDataSource.getCurrenWeatherByCityName(cityName, apiKey, lang, units)
    }

    override suspend fun getCurrenWeatherByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): CurrentWeather {
        return RemoteDataSource.getCurrenWeatherByCityAndCountry(query, apiKey, lang, units)
    }

    override suspend fun getForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return RemoteDataSource.getForecastByLatLon(lat, lon, apiKey, lang, units)
    }

    override suspend fun getForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return RemoteDataSource.getForecastByCityName(cityName, apiKey, lang, units)
    }

    override suspend fun getForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return RemoteDataSource.getForecastByCityAndCountry(query, apiKey, lang, units)
    }

    override suspend fun getHourlyForecastByLatLon(
        lat: Double,
        lon: Double,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return RemoteDataSource.getHourlyForecastByLatLon(lat, lon, apiKey, lang, units)
    }

    override suspend fun getHourlyForecastByCityName(
        cityName: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return RemoteDataSource.getHourlyForecastByCityName(cityName, apiKey, lang, units)
    }

    override suspend fun getHourlyForecastByCityAndCountry(
        query: String,
        apiKey: String,
        lang: String?,
        units: String?
    ): ForecastResponse {
        return RemoteDataSource.getHourlyForecastByCityAndCountry(query, apiKey, lang, units)
    }


}