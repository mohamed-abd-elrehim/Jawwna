package com.example.jawwna.datasource.repository

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jawwna.BuildConfig
import com.example.jawwna.datasource.localdatasource.FakeLocalDataSource
import com.example.jawwna.datasource.model.City
import com.example.jawwna.datasource.model.Clouds
import com.example.jawwna.datasource.model.Coord
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.FavoriteLocationModel
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.Main
import com.example.jawwna.datasource.model.Rain
import com.example.jawwna.datasource.model.Temp
import com.example.jawwna.datasource.model.TemperatureResult
import com.example.jawwna.datasource.model.WeatherCondition
import com.example.jawwna.datasource.model.WeatherList
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.model.Wind
import com.example.jawwna.datasource.remotedatasoure.FakeRemoteDataSource
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest= Config.NONE)
@RunWith(AndroidJUnit4::class)
class RepositoryTest{
    private lateinit var repository: Repository
    private lateinit var fakeLocalDataSource: FakeLocalDataSource
    private lateinit var fakeRemoteDataSource: FakeRemoteDataSource
    private lateinit var application: Application

    @Before
    fun setup() {
        application = ApplicationProvider.getApplicationContext() // Get application context
        fakeLocalDataSource = FakeLocalDataSource()
        fakeRemoteDataSource = FakeRemoteDataSource()
        repository = Repository.getRepository(application) // Pass a mock or fake application if needed

    }

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

    // Define the FavoriteLocationModel data class

    // Create an instance of TemperatureResult
    val fakeTemperatureResult = TemperatureResult(value = 295.0, unit = "K")

    // Create an instance of FavoriteLocationModel using the TemperatureResult
    val fakeFavoriteModel = FavoriteLocationModel(
        placeName = "Cairo",
        icon = "01d",
        description = "clear sky",
        temp = fakeTemperatureResult,
        lat = "30.0444",
        lon = "31.2357"
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


    val fakeWeatherResponseEntity = WeatherResponseEntity(
        cityName = "Test City",
        currentWeatherList = listOf(fakeCurrentWeather),
        dailyForecastList = listOf(weatherResponse),
        hourlyForecastList = listOf(fakeForecastResponse),
        latitude = 30.0444,
        longitude = 31.2357,
        displayName = "Cairo, Egypt"
    )

    val fakeWeatherResponseEntity2 = FavoriteWeatherEntity(
        cityName = "Alexandria",
        currentWeatherList = listOf(fakeCurrentWeather),
        dailyForecastList = listOf(weatherResponse),
        hourlyForecastList = listOf(fakeForecastResponse),
        latitude = 31.2156,
        longitude = 29.9553,
        displayName = "Alexandria, Egypt"
    )


    @Test
    fun testInsertAndGetWeatherLocalData() = runTest {
        repository.insertWeatherLocalData(fakeWeatherResponseEntity)

        val retrievedWeather = repository.getWeatherLocalData("Test City")
        assertEquals(fakeWeatherResponseEntity, retrievedWeather)
    }
    @Test
    fun testInsertAndGetFavoriteWeatherWeatherLocalData() = runTest {
        repository.insertFavoriteWeather(fakeWeatherResponseEntity2)

        val retrievedWeather = repository.getFavoriteWeather("Alexandria")
        assertEquals(fakeWeatherResponseEntity2, retrievedWeather)
    }
    @Test
    fun testGetCurrenWeatherByLatLon() = runBlocking {
        val lat = 30.0128 // Example latitude
        val lon = 31.2497 // Example longitude
        val expectedCityName = "Giza" // Adjusted expected name

        val currentWeather = repository.getCurrenWeatherByLatLon(lat, lon, BuildConfig.OPEN_WEATHER_API_KEY_PRO, null, null)

        // Assert that the returned current weather matches the fake data
        assertEquals(expectedCityName, currentWeather.name)
        // Add more assertions as needed
    }




}