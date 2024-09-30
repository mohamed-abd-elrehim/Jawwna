package com.example.jawwna.add_favorite_location_screen.viewmodel

import androidx.lifecycle.Observer
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jawwna.datasource.model.City
import com.example.jawwna.datasource.model.Clouds
import com.example.jawwna.datasource.model.Coord
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.FavoriteLocationModel
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.LocationDataHolder
import com.example.jawwna.datasource.model.Main
import com.example.jawwna.datasource.model.Rain
import com.example.jawwna.datasource.model.Temp
import com.example.jawwna.datasource.model.TemperatureResult
import com.example.jawwna.datasource.model.WeatherCondition
import com.example.jawwna.datasource.model.WeatherList
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.model.Wind
import com.example.jawwna.datasource.repository.FakeRepository
import com.example.jawwna.datasource.repository.IRepository
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.bouncycastle.math.ec.ECCurve
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@Config(manifest= Config.NONE)
@RunWith(AndroidJUnit4::class)
class AddFavoriteLocationViewModelTest {
    lateinit var tasksViewModel: AddFavoriteLocationViewModel
    val testRepository = FakeRepository(mutableListOf())

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
    //List of FavoriteWeatherEntity
    val favoriteWeatherList = mutableListOf(
        FavoriteWeatherEntity(
            cityName = "Cairo",
            currentWeatherList = listOf(fakeCurrentWeather),
            dailyForecastList = listOf(weatherResponse),
            hourlyForecastList = listOf(fakeForecastResponse),
            latitude = 30.0444,
            longitude = 31.2357,
            displayName = "Cairo, Egypt"
        ),
        FavoriteWeatherEntity(
            cityName = "Alexandria",
            currentWeatherList = listOf(fakeCurrentWeather),
            dailyForecastList = listOf(weatherResponse),
            hourlyForecastList = listOf(fakeForecastResponse),
            latitude = 31.2156,
            longitude = 29.9553,
            displayName = "Alexandria, Egypt"
        ),
        FavoriteWeatherEntity(
            cityName = "Giza",
            currentWeatherList = listOf(fakeCurrentWeather),
            dailyForecastList = listOf(weatherResponse),
            hourlyForecastList = listOf(fakeForecastResponse),
            latitude = 30.0131,
            longitude = 31.2089,
            displayName = "Giza, Egypt"
        )
        // Add more FavoriteWeatherEntity instances as needed
    )

    @Before
    fun setup() {
        //testRepository = FakeRepository(mutableListOf())
        tasksViewModel = AddFavoriteLocationViewModel(testRepository)

    }
    @Test
    fun testAddFavoriteLocation() = runTest {
        // Given
        // When
        testRepository.insertFavoriteWeather(favoriteWeatherList.get(0))
        tasksViewModel.deleteFavoriteWeather(fakeFavoriteModel)

        // Then
        assertTrue(testRepository.fake.isEmpty())
    }



    @Test
    fun `test updateFavoriteWeatherData inserts correct data`() = runTest {
        // Create a fake LocationDataHolder
        val locationData = LocationDataHolder(
            latitude = 30.0444,
            longitude = 31.2357,
            locationName = "Cairo"
        )

        // Call the function to update favorite weather
        tasksViewModel.updateFavoriteWeatherData(locationData)

        // Collect the data from the repository to assert the values
        val expectedWeather = testRepository.fakeFavoriteWeather
        testRepository.getAllFavoriteWeather().collect { favoriteWeatherList ->
            // Check if the inserted data matches the expected data
            assertEquals(expectedWeather.cityName, favoriteWeatherList[0].cityName)
            assertEquals(expectedWeather.latitude, favoriteWeatherList[0].latitude, 0.0)
            assertEquals(expectedWeather.longitude, favoriteWeatherList[0].longitude, 0.0)
        }
    }
}





