package com.example.jawwna.homescreen.viewmodel

import android.app.Application
import android.content.res.Configuration
import android.content.res.Resources
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.R
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.PreferencesCurrentLocationHelper
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.DailyForecastData
import com.example.jawwna.datasource.model.ForecastResponse
import com.example.jawwna.datasource.model.HourlyForecastData
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.location.PreferencesLocationHelper
import com.example.jawwna.datasource.localdatasoource.shared_preferences_helper.settings.PreferencesSettingsHelper
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.remotedatasource.ApiResponse
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.helper.PreferencesLocationEum
import com.example.jawwna.helper.TemperatureUnits
import com.example.jawwna.helper.UnitConvertHelper
import com.example.jawwna.helper.WindSpeedUnits
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeViewModel( private val repository: IRepository) :ViewModel() {

    private val TAG = "HomeViewModel"


    // LiveData to hold the card settings field background color
    private val _cardSettingsFieldBackgroundLightMode = MutableStateFlow<Int>(0)
    val cardSettingsFieldBackgroundLightModeLiveData: StateFlow<Int> get() = _cardSettingsFieldBackgroundLightMode

    // LiveData to observe theme mode changes
    private val _isDarkMode = MutableStateFlow<Boolean>(false)
    val isDarkMode: StateFlow<Boolean> get() = _isDarkMode


    //LiveDataGetWhterData

    private val _currentWeatherData = MutableStateFlow<ApiResponse<CurrentWeather>>(ApiResponse.Loading)
    val currentWeatherData: StateFlow<ApiResponse<CurrentWeather>> = _currentWeatherData

    //LiveDataGetWhterData
    private val _weatherForecastHourlyData = MutableStateFlow<ApiResponse< ForecastResponse>>(
        ApiResponse.Loading)
    val weatherForecastHourlyData: StateFlow<ApiResponse<ForecastResponse>> = _weatherForecastHourlyData

    //LiveDataGetWhterData
    private val _weatherForecast16DailyData = MutableStateFlow<ApiResponse<WeatherResponse>>(
        ApiResponse.Loading)
    val weatherForecast16DailyData: StateFlow<ApiResponse<WeatherResponse>> = _weatherForecast16DailyData

    // _weatherForecast16DailyRow
    private val _weatherForecast16DailyRow = MutableStateFlow<List<DailyForecastData>>(
        emptyList()
    )
    val weatherForecast16DailyRow: StateFlow<List<DailyForecastData>> = _weatherForecast16DailyRow

    //_weatherForecastHourlyRow
    private val _weatherForecastHourlyRow = MutableStateFlow<List<HourlyForecastData>>(
        emptyList()
    )
    val weatherForecastHourlyRow: StateFlow<List<HourlyForecastData>> = _weatherForecastHourlyRow


    private var weatherResponseEntity: WeatherResponseEntity = WeatherResponseEntity("",emptyList(),emptyList(),emptyList(),0.0,0.0)


    fun fetchWeatherForecastHourlyData(apiKey: String) {
        viewModelScope.launch {
            try {
                repository.execute(PreferencesLocationEum.CURRENT)
                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()

                _weatherForecastHourlyData.value = ApiResponse.Loading
                repository.getHourlyForecastByLatLon(lat, lon, apiKey, null, null).collect{data->
                    val hourlyForecastDataList = mapToHourlyForecastData(data)
                    _weatherForecastHourlyRow.value = hourlyForecastDataList
                    Log.d(TAG, "fetchWeatherForecastHourlyData: $hourlyForecastDataList")

                    _weatherForecastHourlyData.value = ApiResponse.Success(data)
                    weatherResponseEntity.hourlyForecastList= listOf(data)

                }
            } catch (e: Exception) {
                _weatherForecastHourlyData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun fetchCurrentWeatherData(apiKey: String) {
        viewModelScope.launch {
            try {
                repository.execute(PreferencesLocationEum.CURRENT)
                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()


                _currentWeatherData.value = ApiResponse.Loading
                val data = repository.getCurrenWeatherByLatLon(lat, lon, apiKey, null, null)
                _currentWeatherData.value = ApiResponse.Success(data)
                repository.setOldTemperatureUnit(TemperatureUnits.metric.toString())
                repository.setOldWindSpeedUnit(WindSpeedUnits.metric.toString())

                weatherResponseEntity.currentWeatherList= listOf(data)

            } catch (e: Exception) {
                _currentWeatherData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun featch16DailyWeatherData(apiKey: String) {
        viewModelScope.launch {
            try {
                _weatherForecast16DailyData.value = ApiResponse.Loading
                repository.execute(PreferencesLocationEum.CURRENT)

                val lat = repository.getLocationLatitude()
                val lon = repository.getLocationLongitude()

                repository.getForecastDailyByLatLon(lat, lon, apiKey, null, null).collect{
                        data->

                    val dailyForecastDataList = mapToDailyForecastData(data)
                    _weatherForecast16DailyRow.value = dailyForecastDataList
                    weatherResponseEntity.dailyForecastList= listOf(data)

                    Log.d(TAG, "featch16DailyWeatherData: $dailyForecastDataList")

                    _weatherForecast16DailyData.value = ApiResponse.Success(data)
                }
            } catch (e: Exception) {
                _weatherForecast16DailyData.value = ApiResponse.Error(e.message ?: "Unknown error")
            }
        }
    }
    fun insertWeatherResponseEntity()
    {
        if (!weatherResponseEntity.currentWeatherList.isEmpty()&&!weatherResponseEntity.dailyForecastList.isEmpty()&&!weatherResponseEntity.hourlyForecastList.isEmpty()) {
            viewModelScope.launch {
                repository.execute(PreferencesLocationEum.CURRENT)
                repository.deleteAllWeatherLocalData()
                weatherResponseEntity.cityName=repository.getLocationName().toString()
                weatherResponseEntity.latitude=repository.getLocationLatitude()
                weatherResponseEntity.longitude=repository.getLocationLongitude()
                Log.d(TAG, "insertWeatherResponseEntity: $weatherResponseEntity")
                repository.insertWeatherLocalData(weatherResponseEntity)
            }
        }
    }





    fun setCardSettingsFieldBackgroundLightMode(packageName: String, nightModeFlags: Int) {
        val colorResId = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> R.drawable.card_settings_field_background_night_mode
            Configuration.UI_MODE_NIGHT_NO -> R.drawable.card_settings_field_background_light_mode
            else -> R.drawable.card_settings_field_background_light_mode
        }
        _cardSettingsFieldBackgroundLightMode.value = colorResId


    }


    fun checkThemeMode(resources: Resources) {
        val isDark = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        _isDarkMode.value = isDark
    }

    fun getTextColor(isDarkMode: Boolean): Int {
        return if (isDarkMode) android.R.color.holo_blue_bright else android.R.color.holo_blue_dark

    }



    //GetFuture17DayNames
    private fun mapToDailyForecastData(response: WeatherResponse): List<DailyForecastData> {
        val dayNames = getFutureDayNames() // Get the future day names

        return response.list

            .take(7) // Take the next 7 elements
            .mapIndexed { index, weatherList ->
                DailyForecastData(
                    dayName = dayNames.getOrNull(index), // Get the corresponding day name (index + 1)
                    icon = weatherList.weather.firstOrNull()?.icon,
                    description = weatherList.weather.firstOrNull()?.description,
                    tempMax = checkTemperatureUnit(weatherList.temp.max),
                    tempMin = checkTemperatureUnit(weatherList.temp.min)

                    //tempUnit = "°K"
                )
            }
            .toList() // Explicitly convert to List
    }



    // Function to get future day names using Calendar, skipping the current day
    private fun getFutureDayNames(daysCount: Int = 7): List<String> {
        val calendar = Calendar.getInstance()
        val dayNames = mutableListOf<String>()

        // Start from the next day
        calendar.add(Calendar.DAY_OF_YEAR, 1)

        for (i in 0 until daysCount) { // Iterate for all daysCount
            dayNames.add(SimpleDateFormat("EEEE", Locale.getDefault()).format(calendar.time))
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        Log.i("dayNames", "$dayNames")
        return dayNames
    }



     fun checkTemperatureUnit(temp:Double): String {
        return when (repository.getTemperatureUnit()) {
            TemperatureUnits.metric.toString() ->return "${UnitConvertHelper.convertTemperature(temp,repository.getOldTemperatureUnit(),TemperatureUnits.metric)}°C"
            TemperatureUnits.imperial.toString()->return "${UnitConvertHelper.convertTemperature(temp,repository.getOldTemperatureUnit(),TemperatureUnits.imperial)}°F"
            else -> "${UnitConvertHelper.convertTemperature(temp,repository.getOldTemperatureUnit(),TemperatureUnits.standard)}°K"
        }
    }
     fun checkWindSpeedUnit(windSpeed:Double): String {
        return when (repository.getWindSpeedUnit()) {
            WindSpeedUnits.metric.toString() ->return "${UnitConvertHelper.convertWindSpeed(windSpeed,repository.getOldWindSpeedUnit(),
                WindSpeedUnits.metric)} m/s"
            else -> "${UnitConvertHelper.convertWindSpeed(windSpeed,repository.getOldWindSpeedUnit(),WindSpeedUnits.imperial)} mph"
        }
    }

    // Function to map CurrentWeather to HourlyForecastData
    private fun mapToHourlyForecastData(response: ForecastResponse): List<HourlyForecastData> {
        // Map the list of CurrentWeather to HourlyForecastData
        return response.list.map { currentWeather ->
            // Convert dt_txt to 12-hour time format with AM/PM
            val time = formatTimeFromDateString(currentWeather.dt_txt)
            // Create and return HourlyForecastData object
            HourlyForecastData(
                icon = currentWeather.weather?.firstOrNull()?.icon,
                Time = time, // Converted time with AM/PM
                temp = currentWeather.main?.temp?.let { checkTemperatureUnit(it) },
                //tempUnit = "°K" // Assuming Kelvin (convert if needed)
            )
        }
    }

    // Function to convert date string to formatted time (AM/PM)
    fun formatTimeFromDateString(dateString: String?): String? {
        // Define date formats for input and output
        val inputDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputTimeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        return dateString?.let { dt ->
            inputDateFormat.parse(dt)?.let { date ->
                outputTimeFormat.format(date) // Convert to 12-hour time with AM/PM
            }
        }
    }

    // Function to get the current date as a string
    fun getCurrentDate(): String {
        // Define the date format
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        // Get the current date
        val currentDate = Calendar.getInstance().time
        // Return the formatted date
        return dateFormat.format(currentDate)
    }


    // Function to get the current time in AM/PM format
    fun getCurrentTime(): String {
        // Define the time format
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        // Get the current time
        val currentTime = Calendar.getInstance().time
        // Return the formatted time
        return timeFormat.format(currentTime)
    }




    /*
        @RequiresApi(Build.VERSION_CODES.O) // Ensure you're using Android API level 26+
        fun getFutureDayNames(): List<String> {
            val currentDate = LocalDate.now()
            val dayNamesList = mutableListOf<String>()

            for (i in 1..16) { // Start from 1 to skip the current day and include the next 16 days
                val futureDate = currentDate.plusDays(i.toLong())
                val dayName = futureDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
                dayNamesList.add(dayName)
            }

            return dayNamesList
        }


        @RequiresApi(Build.VERSION_CODES.O)
        fun mapToDailyForecastData(response: WeatherResponse): List<DailyForecastData> {
            val dayNames = getFutureDayNames() // Get the future day names

            return response.list.mapIndexed { index, weatherList ->
                DailyForecastData(
                    dayName = dayNames.getOrNull(index), // Get the corresponding day name
                    icon = weatherList.weather.firstOrNull()?.icon,
                    description = weatherList.weather.firstOrNull()?.description,
                    tempMax = weatherList.temp.max,
                    tempMin = weatherList.temp.min,
                    tempUnit = "°K"
                )
            }.toList() // Explicitly convert to List
        }
    */


}
