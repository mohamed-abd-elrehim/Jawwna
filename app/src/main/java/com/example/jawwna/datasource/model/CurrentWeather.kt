package com.example.jawwna.datasource.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


/*
Represents the forecast response from the API.
@param list List of weather forecasts for each hour or time period.
@param city Embedding city data.
 */
data class ForecastResponse(
    @TypeConverters(WeatherDataConverter::class)
    val list: List<CurrentWeather>, // List of weather forecasts for each hour or time period
    @Embedded val city: City // Embedding city data
)

/**
 * Data class representing a weather response from the API.
 *
 * @property city Information about the city for which the weather is forecasted.
 * @property cod Response code from the API (e.g., "200" for success).
 * @property message Additional message from the API (usually a numeric value).
 * @property list List of weather forecasts for the specified city.
 */
data class WeatherResponse(
    @Embedded val city: City,
    val cod: String,
    val message: Double,
    val cnt: Int,
    @TypeConverters(WeatherListConverter::class)
    val list: List<WeatherList>
)

@Entity(tableName = "current_weather")
data class CurrentWeather(
    @PrimaryKey val id: Long, // Unique identifier for the location (city ID)
    val name: String,//City name. Please note that built-in geocoder functionality has been deprecated. Learn more here
    @Embedded val coord: Coord, // Embedded object for coordinates
    @TypeConverters(WeatherConditionConverter::class) val weather: List<WeatherCondition>, // Converted list of weather conditions
    @Embedded val main: Main, // Embedded object for main weather data
    @Embedded val wind: Wind, // Embedded object for wind information
    @Embedded val clouds: Clouds, // Embedded object for cloud information
    @Embedded val rain: Rain?,
    val dt_txt: String?


)


/**
 * Data class representing the weather for fav city response from the API, now as a Room Entity.
 */
@Entity(tableName = "favorite_locations")
data class FavoriteLocation(
    @PrimaryKey val id: Long, // Unique identifier for the location (city ID)
    val name: String,//City name. Please note that built-in geocoder functionality has been deprecated. Learn more here
    @Embedded val coord: Coord, // Embedded object for coordinates
    @TypeConverters(WeatherConditionConverter::class) val weather: List<WeatherCondition>, // Converted list of weather conditions
    @Embedded val main: Main, // Embedded object for main weather data
    @Embedded val wind: Wind, // Embedded object for wind information
    @Embedded val clouds: Clouds, // Embedded object for cloud information
    @Embedded val rain: Rain?,
    val dt_txt: String?
)

/**
 * Data class representing a weather forecast.
 *
 * @property dt Timestamp of the forecast.
 * @property sunrise Sunrise time for the forecast location.
 * @property sunset Sunset time for the forecast location.
 * @property temp Temperature data for the forecast period.
 * @property pressure Atmospheric pressure at the forecast time (in hPa).
 * @property humidity Humidity percentage at the forecast time.
 * @property weather List of weather conditions affecting the forecast.
 * @property speed Wind speed (in meters per second).
 * @property deg Wind direction (in degrees).
 * @property gust Wind gust speed (in meters per second).
 * @property clouds Cloudiness percentage at the forecast time.
 * @property pop Probability of precipitation (from 0 to 1).
 * @property rain Amount of rain (in mm) during the forecast period; null if no rain is expected.
 */
/**
 * Data class representing a weather forecast.
 */
data class WeatherList(

    @Embedded val temp: Temp,
    val pressure: Int,
    val humidity: Int,
    @TypeConverters(WeatherConditionConverter::class) val weather: List<WeatherCondition>,
    val speed: Double,
    val clouds: Int,
    val rain: Double?
)

/**
 * Data class representing temperature information.
 *
 * @property day Temperature during the day.
 * @property min Minimum temperature for the day.
 * @property max Maximum temperature for the day.
 * @property night Temperature during the night.
 * @property eve Temperature during the evening.
 * @property morn Temperature during the morning.
 */
/**
 * Data class representing temperature data.
 */
data class Temp(
    val day: Double,
    val min: Double,
    val max: Double,
    val night: Double,
    val eve: Double,
    val morn: Double
)




/**
 * Represents city information.
 *
 * @param id City ID.
 * @param name City name.
 * @param coord City coordinates.
 * @param country Country code.
 * @param population Population count.
 * @param timezone Timezone offset in seconds.
 * @param sunrise Sunrise time (UNIX timestamp).
 * @param sunset Sunset time (UNIX timestamp).
 */
data class City(

    val name: String,
    val coord: Coord

)


/**
 * Data class representing the weather for current location response from the API, now as a Room Entity.
 */
/**
 * Data class representing the weather response from the API.
 *
 * @param coord Geographical coordinates (latitude and longitude).
 * @param WeatherCondition List of weather conditions.
 * @param base Internal parameter used by the weather service (e.g., "stations").
 * @param main Main weather information including temperature, pressure, and humidity.
 * @param visibility Visibility in meters.
 * @param wind Wind information such as speed and direction.
 * @param clouds Cloudiness percentage.
 * @param dt Time of the weather data calculation (Unix timestamp).
 * @param sys Additional system-related information like country and sunrise/sunset.
 * @param timezone Timezone offset from UTC in seconds.
 * @param id Unique identifier for the location (e.g., city ID).
 * @param name Name of the location (e.g., city name).
 * @param cod Response status code (e.g., 200 for success).'
 * @param rain Rain information (nullable).
 * @param dt_txt Formatted date and time.
 * @param pop Probability of precipitation.
 */


/**
 * Data class representing geographical coordinates.
 *r
 * @param lon Longitude of the location.
 * @param lat Latitude of the location.
 */

/**
 * Data class representing geographical coordinates, embedded in `CurrentWeather`.
 */
data class Coord(
    val lon: Double,
    val lat: Double
)

/**
 * Data class representing the weather condition.
 *
 * @param id Weather condition ID (used for internal mapping).
 * @param main Group of weather parameters (e.g., "Clouds", "Rain").
 * @param description Description of the weather condition (e.g., "overcast clouds").
 * @param icon Icon ID for representing the weather condition visually (e.g., "04n").
 */
/**
 * Data class representing the weather condition, now as a Room Entity.
 */

@Entity(tableName = "WeatherCondition")
data class WeatherCondition(
    @PrimaryKey val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

/**
 * Represents rain information.
 *
 * @param `1h` Rain volume for the last hour (nullable).
 *  @param `3h` Rain volume for the last 3 hours (nullable).
 *
 */

data class Rain(
    val `1h`: Double? = null,  // Nullable Double for 1 hour of rain
    val `3h`: Double? = null   // Nullable Double for 3 hours of rain
)

/**
 * Data class representing main weather data.
 *
 * @param temp Current temperature in Kelvin.
 * @param feels_like Perceived temperature considering factors like wind and humidity.
 * @param temp_min Minimum temperature at the moment (in Kelvin).
 * @param temp_max Maximum temperature at the moment (in Kelvin).
 * @param pressure Atmospheric pressure in hPa (hectopascals).
 * @param humidity Humidity percentage (0-100).
 * @param sea_level Atmospheric pressure at sea level (in hPa).
 * @param grnd_level Atmospheric pressure at ground level (in hPa).
 * @param temp_kf Temperature range forecast (in Kelvin).
 */

/**
 * Data class representing main weather data, embedded in `CurrentWeather`.
 */
data class Main(
    val temp: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,

)

/**
 * Data class representing wind conditions.
 *
 * @param speed Wind speed in meters per second (m/s).
 * @param deg Wind direction in degrees (meteorological).
 * @param gust Wind gust speed in meters per second (m/s).
 */
/**
 * Data class representing wind conditions, embedded in `CurrentWeather`.
 */
data class Wind(
    val speed: Double
)


/**
 * Data class representing cloud coverage.
 *
 * @param all Cloudiness percentage (0-100).
 */
/**
 * Data class representing cloud coverage, embedded in `CurrentWeather`.
 */
data class Clouds(
    val all: Int
)

/**
 * Converter for WeatherList.
 */
class WeatherListConverter {
    @TypeConverter
    fun fromWeatherList(weatherList: List<WeatherList>): String {
        return Gson().toJson(weatherList)
    }

    @TypeConverter
    fun toWeatherList(weatherListString: String): List<WeatherList> {
        val listType = object : TypeToken<List<WeatherList>>() {}.type
        return Gson().fromJson(weatherListString, listType)
    }
}


class WeatherConditionConverter {
    @TypeConverter
    fun fromWeatherList(weather: List<WeatherCondition>): String {
        return Gson().toJson(weather)
    }

    @TypeConverter
    fun toWeatherList(weather: String): List<WeatherCondition> {
        val listType = object : TypeToken<List<WeatherCondition>>() {}.type
        return Gson().fromJson(weather, listType)
    }
}

class WeatherDataConverter {
    @TypeConverter
    fun fromWeatherList(weather: List<CurrentWeather>): String {
        return Gson().toJson(weather)
    }

    @TypeConverter
    fun toWeatherList(weather: String): List<CurrentWeather> {
        val listType = object : TypeToken<List<CurrentWeather>>() {}.type
        return Gson().fromJson(weather, listType)
    }
}