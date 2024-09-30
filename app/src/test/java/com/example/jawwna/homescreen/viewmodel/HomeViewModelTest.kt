package com.example.jawwna.homescreen.viewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.jawwna.datasource.model.City
import com.example.jawwna.datasource.model.Clouds
import com.example.jawwna.datasource.model.Coord
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.DailyForecastData
import com.example.jawwna.datasource.model.FavoriteLocationModel
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
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.FakeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config


@Config(manifest = Config.NONE)
@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    lateinit var tasksViewModel: HomeViewModel
    val testRepository = FakeRepository(mutableListOf())

    @Before
    fun setup() {
        tasksViewModel = HomeViewModel(testRepository)
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
        cityName = "Cairo",
        currentWeatherList = listOf(fakeCurrentWeather),
        dailyForecastList = listOf(weatherResponse),
        hourlyForecastList = listOf(fakeForecastResponse),
        latitude = 30.0444,
        longitude = 31.2357,
        displayName = "Cairo, Egypt"
    )

    val fakeWeatherResponseEntity2 = WeatherResponseEntity(
        cityName = "Alexandria",
        currentWeatherList = listOf(fakeCurrentWeather),
        dailyForecastList = listOf(weatherResponse),
        hourlyForecastList = listOf(fakeForecastResponse),
        latitude = 31.2156,
        longitude = 29.9553,
        displayName = "Alexandria, Egypt"
    )



    @Test
    fun testMapToFavoriteDataLatLong() {
        // Arrange: Create sample input data
        val response = listOf(fakeWeatherResponseEntity, fakeWeatherResponseEntity2)

        // Act: Call the function
        val result =  tasksViewModel.mapToFavoriteDataLatLong(response)

        // Assert: Verify the output
        val expected = mutableListOf(
            LocationDataHolder(latitude = 30.0444, longitude = 31.2357, locationName = "Cairo",null),
            LocationDataHolder(latitude = 30.0444, longitude = 31.2357, locationName = "Alexandria",null)
        )

        assertEquals(expected, result)
    }


    val weatherResponseList = WeatherResponse(
        city = City(name = "Cairo", coord = coord),
        cod = "200",
        message = 0.0,
        cnt = 7,
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
            ),
            WeatherList(
                temp = Temp(302.0, 292.0, 312.0, 296.0, 299.0, 294.0),
                pressure = 1010,
                humidity = 55,
                weather = listOf(weatherCondition),
                speed = 3.0,
                clouds = 20,
                rain = null,
                dt = System.currentTimeMillis() / 1000 + 86400
            ),
            WeatherList(
                temp = Temp(298.0, 288.0, 308.0, 291.0, 293.0, 290.0),
                pressure = 1008,
                humidity = 60,
                weather = listOf(weatherCondition),
                speed = 1.5,
                clouds = 30,
                rain = null,
                dt = System.currentTimeMillis() / 1000 + 86400 * 2
            ),
            WeatherList(
                temp = Temp(295.0, 285.0, 305.0, 289.0, 292.0, 287.0),
                pressure = 1005,
                humidity = 65,
                weather = listOf(weatherCondition),
                speed = 2.0,
                clouds = 40,
                rain = null,
                dt = System.currentTimeMillis() / 1000 + 86400 * 3
            ),
            WeatherList(
                temp = Temp(294.0, 284.0, 304.0, 288.0, 291.0, 286.0),
                pressure = 1007,
                humidity = 70,
                weather = listOf(weatherCondition),
                speed = 4.0,
                clouds = 50,
                rain = 1.0, // Assuming some rain amount
                dt = System.currentTimeMillis() / 1000 + 86400 * 4
            ),
            WeatherList(
                temp = Temp(296.0, 286.0, 306.0, 290.0, 293.0, 288.0),
                pressure = 1006,
                humidity = 75,
                weather = listOf(weatherCondition),
                speed = 3.5,
                clouds = 60,
                rain = 5.0, // Assuming some rain amount
                dt = System.currentTimeMillis() / 1000 + 86400 * 5
            ),
            WeatherList(
                temp = Temp(299.0, 289.0, 309.0, 293.0, 296.0, 291.0),
                pressure = 1004,
                humidity = 80,
                weather = listOf(weatherCondition),
                speed = 2.0,
                clouds = 70,
                rain = null,
                dt = System.currentTimeMillis() / 1000 + 86400 * 6
            )
        )
    )


    @Test
    fun testMapToDailyForecastData() {
        // Prepare mock data for WeatherResponse
        val weatherResponse = WeatherResponse(
            city = City(name = "Cairo", coord = Coord(lon = 31.2357, lat = 30.0444)),
            cod = "200",
            message = 0.0,
            cnt = 7,
            list = listOf(
                WeatherList(weather = listOf(WeatherCondition(icon = "01d", description = "clear sky", id = 800, main = "Clear")), temp = Temp(max = 26.85, min = 16.85, day = 0.0, eve = 0.0, night = 0.0, morn = 0.0), pressure = 1013, humidity = 40, speed = 5.0, clouds = 10, rain = null, dt = 1630460400),
                WeatherList(weather = listOf(WeatherCondition(icon = "01d", description = "clear sky", id = 800, main = "Clear")), temp = Temp(max = 28.85, min = 18.85, day = 0.0, eve = 0.0, night = 0.0, morn = 0.0), pressure = 1013, humidity = 35, speed = 6.0, clouds = 5, rain = null, dt = 1630546800),
                WeatherList(weather = listOf(WeatherCondition(icon = "01d", description = "clear sky", id = 800, main = "Clear")), temp = Temp(max = 25.85, min = 15.85, day = 0.0, eve = 0.0, night = 0.0, morn = 0.0), pressure = 1012, humidity = 30, speed = 4.5, clouds = 15, rain = null, dt = 1630633200),
                WeatherList(weather = listOf(WeatherCondition(icon = "01d", description = "clear sky", id = 800, main = "Clear")), temp = Temp(max = 22.85, min = 12.85, day = 0.0, eve = 0.0, night = 0.0, morn = 0.0), pressure = 1011, humidity = 25, speed = 3.0, clouds = 20, rain = null, dt = 1630719600),
                WeatherList(weather = listOf(WeatherCondition(icon = "01d", description = "clear sky", id = 800, main = "Clear")), temp = Temp(max = 21.85, min = 11.85, day = 0.0, eve = 0.0, night = 0.0, morn = 0.0), pressure = 1010, humidity = 28, speed = 2.5, clouds = 30, rain = null, dt = 1630806000),
                WeatherList(weather = listOf(WeatherCondition(icon = "01d", description = "clear sky", id = 800, main = "Clear")), temp = Temp(max = 23.85, min = 13.85, day = 0.0, eve = 0.0, night = 0.0, morn = 0.0), pressure = 1012, humidity = 33, speed = 4.0, clouds = 15, rain = null, dt = 1630892400),
                WeatherList(weather = listOf(WeatherCondition(icon = "01d", description = "clear sky", id = 800, main = "Clear")), temp = Temp(max = 26.85, min = 16.85, day = 0.0, eve = 0.0, night = 0.0, morn = 0.0), pressure = 1013, humidity = 39, speed = 5.0, clouds = 10, rain = null, dt = 1630978800)
            )
        )

        // Call the function under test
        val result = tasksViewModel.mapToDailyForecastData(weatherResponse)

        // Prepare expected output
        val expected = listOf(
            DailyForecastData(dayName = "Tuesday", icon = "01d", description = "clear sky", tempMax = TemperatureResult(value = 26.9, unit = "°C"), tempMin = TemperatureResult(value = 16.9, unit = "°C")),
            DailyForecastData(dayName = "Wednesday", icon = "01d", description = "clear sky", tempMax = TemperatureResult(value = 28.9, unit = "°C"), tempMin = TemperatureResult(value = 18.9, unit = "°C")),
            DailyForecastData(dayName = "Thursday", icon = "01d", description = "clear sky", tempMax = TemperatureResult(value = 25.9, unit = "°C"), tempMin = TemperatureResult(value = 15.8, unit = "°C")),
            DailyForecastData(dayName = "Friday", icon = "01d", description = "clear sky", tempMax = TemperatureResult(value = 22.9, unit = "°C"), tempMin = TemperatureResult(value = 12.8, unit = "°C")),
            DailyForecastData(dayName = "Saturday", icon = "01d", description = "clear sky", tempMax = TemperatureResult(value = 21.9, unit = "°C"), tempMin = TemperatureResult(value = 11.8, unit = "°C")),
            DailyForecastData(dayName = "Sunday", icon = "01d", description = "clear sky", tempMax = TemperatureResult(value = 23.9, unit = "°C"), tempMin = TemperatureResult(value = 13.8, unit = "°C")),
            DailyForecastData(dayName = "Monday", icon = "01d", description = "clear sky", tempMax = TemperatureResult(value = 26.9, unit = "°C"), tempMin = TemperatureResult(value = 16.9, unit = "°C"))
        )


        // Assert the result matches expected
        assertEquals(expected, result)
    }


}





