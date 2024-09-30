package com.example.jawwna.datasource.remotedatasoure

import com.example.jawwna.datasource.model.City
import com.example.jawwna.datasource.model.Clouds
import com.example.jawwna.datasource.model.Coord
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.Main
import com.example.jawwna.datasource.model.Rain
import com.example.jawwna.datasource.model.Temp
import com.example.jawwna.datasource.model.WeatherCondition
import com.example.jawwna.datasource.model.WeatherList
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.model.Wind
import com.example.jawwna.datasource.remotedatasource.IRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FakeRemoteDataSource : IRemoteDataSource {

    private val currentWeatherData = mutableMapOf<String, CurrentWeather>()
    private val forecastData = mutableMapOf<String, ForecastResponse>()
    private val weatherResponseData = mutableMapOf<String, WeatherResponse>()



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

    override suspend fun getCurrenWeatherByLatLon(lat: Double, lon: Double, apiKey: String, lang: String?, units: String?): CurrentWeather {
        // Return fake data based on lat and lon
        return currentWeatherData["$lat,$lon"] ?: fakeCurrentWeather
    }

    override suspend fun getCurrenWeatherByCityName(cityName: String, apiKey: String, lang: String?, units: String?): CurrentWeather {
        // Return fake data based on city name
        return currentWeatherData[cityName] ?: fakeCurrentWeather
    }

    override suspend fun getCurrenWeatherByCityAndCountry(query: String, apiKey: String, lang: String?, units: String?): CurrentWeather {
        // Return fake data based on query
        return currentWeatherData[query] ?: fakeCurrentWeather
    }

    override suspend fun getForecastByLatLon(lat: Double, lon: Double, apiKey: String, lang: String?, units: String?): Flow<ForecastResponse> {
        return flow {
            emit(forecastData["$lat,$lon"] ?: fakeForecastResponse)
        }
    }

    override suspend fun getForecastByCityName(cityName: String, apiKey: String, lang: String?, units: String?): Flow<ForecastResponse> {
        return flow {
            emit(forecastData[cityName] ?:  fakeForecastResponse)
        }
    }

    override suspend fun getForecastByCityAndCountry(query: String, apiKey: String, lang: String?, units: String?): Flow<ForecastResponse> {
        return flow {
            emit(forecastData[query] ?:  fakeForecastResponse)
        }
    }

    override suspend fun getHourlyForecastByLatLon(lat: Double, lon: Double, apiKey: String, lang: String?, units: String?): Flow<ForecastResponse> {
        return flow {
            emit(forecastData["${lat},${lon}_hourly"] ?:  fakeForecastResponse)
        }
    }

    override suspend fun getHourlyForecastByCityName(cityName: String, apiKey: String, lang: String?, units: String?): Flow<ForecastResponse> {
        return flow {
            emit(forecastData["${cityName}_hourly"] ?:  fakeForecastResponse)
        }
    }

    override suspend fun getHourlyForecastByCityAndCountry(query: String, apiKey: String, lang: String?, units: String?): Flow<ForecastResponse> {
        return flow {
            emit(forecastData["${query}_hourly"] ?:  fakeForecastResponse)
        }
    }

    override suspend fun getForecastDailyByLatLon(lat: Double, lon: Double, apiKey: String, lang: String?, units: String?): Flow<WeatherResponse> {
        return flow {
            emit(weatherResponseData["$lat,$lon"] ?: weatherResponse)
        }
    }

    override suspend fun getForecastDailyByCityName(cityName: String, apiKey: String, lang: String?, units: String?): Flow<WeatherResponse> {
        return flow {
            emit(weatherResponseData[cityName] ?: weatherResponse)
        }
    }

    // You may want to provide methods to set fake data for testing
    fun addCurrentWeather(cityName: String, weather: CurrentWeather) {
        currentWeatherData[cityName] = weather
    }

    fun addForecast(cityName: String, forecast: ForecastResponse) {
        forecastData[cityName] = forecast
    }

    fun addWeatherResponse(cityName: String, weatherResponse: WeatherResponse) {
        weatherResponseData[cityName] = weatherResponse
    }
}
