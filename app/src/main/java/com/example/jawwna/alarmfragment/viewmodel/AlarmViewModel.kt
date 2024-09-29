package com.example.jawwna.alarmfragment.viewmodel

import android.util.Log
import androidx.constraintlayout.helper.widget.Flow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jawwna.datasource.model.AlarmDataHolder
import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.model.DailyForecastData
import com.example.jawwna.datasource.model.FavoriteLocationModel
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.WeatherResponse
import com.example.jawwna.datasource.model.WeatherResponseEntity
import com.example.jawwna.datasource.repository.IRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.text.SimpleDateFormat
import java.util.*
class AlarmViewModel(private val repository: IRepository) : ViewModel() {

    private val _alarms = MutableStateFlow<List<AlarmEntity>>(emptyList())
    val alarms: StateFlow<List<AlarmEntity>> get() = _alarms
    private val _currentWeather = MutableStateFlow<List<WeatherResponseEntity>>(emptyList())
    // StateFlow to hold the formatted date list
    private val _dateStateFlow = MutableStateFlow<List<String>>(emptyList())
    val dateStateFlow: StateFlow<List<String>> = _dateStateFlow

    private val _alarmsDetaHolder = MutableStateFlow<AlarmDataHolder?>(null)
    val alarmsDetaHolder: StateFlow<AlarmDataHolder?> get() = _alarmsDetaHolder

    private val selectedDate = MutableStateFlow<String?>(null)
    val _alarmType = MutableStateFlow<String?>(null)

    init {
        loadAlarms()
        getCurrentWeatherData()

    }

    fun loadAlarms() {
        viewModelScope.launch {
            repository.getAllAlarms().collect { alarms ->
                _alarms.value = alarms
            }
        }
    }

    fun saveAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.insertAlarm(alarm)
            loadAlarms()  // Refresh alarms after saving
        }
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            repository.deleteAlarm(alarm)
            loadAlarms()  // Refresh alarms after deleting
        }
    }

//    private fun mapToDailyForecastData(weatherData: List<WeatherResponseEntity>): MutableList<String> {
//
//        return weatherData.flatMap { weatherEntity ->
//            weatherEntity.dailyForecastList.flatMap { dailyForecast ->
//                dailyForecast.list.map { dailyForecastData ->
//                    dailyForecastData.dt.toString() // Converting 'dt' (Unix timestamp) to String
//                }
//            }
//        }.toMutableList() // Convert to MutableList
//    }


    private fun updateWeatherData(weatherData: List<WeatherResponseEntity>) {
        // SimpleDateFormat for formatting dates
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        // Map weather data to a list of formatted date strings
        val formattedDates = weatherData.flatMap { weatherEntity ->
            weatherEntity.dailyForecastList.flatMap { dailyForecast ->
                dailyForecast.list.map { dailyForecastData ->
                    // Convert 'dt' (Unix timestamp) to a human-readable date string
                    val date = Date(dailyForecastData.dt * 1000) // Convert seconds to milliseconds
                    dateFormat.format(date) // Format the date
                }
            }
        }

        // Update the StateFlow with the new list of formatted dates
        _dateStateFlow.value = formattedDates
    }

    fun setAlarmType(type: String) {
        // Update the StateFlow with the selected alarm type
        _alarmType.value = type
    }


    fun setSelectedDate(date: String) {
        // Update the StateFlow with the selected date
        selectedDate.value = date
        getAlarmDataHolder(date)
    }

    private fun getAlarmDataHolder(date: String) {
        // SimpleDateFormat for formatting dates
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) // Use "yyyy-MM-dd" for exact date comparison
        //val  date= selectedDate.value
        val weatherData = _currentWeather.value
        //val parsedDate = dateFormat.parse(date) // Parse the input date

        // Initialize a variable to hold the matching AlarmDataHolder
        var alarmDataHolder: AlarmDataHolder? = null
        // Loop through weather data
        weatherData.forEach { weatherEntity ->
            weatherEntity.dailyForecastList.forEach { dailyForecast ->
                dailyForecast.list.forEach { dailyForecastData ->
                    // Convert the timestamp to a date
                    val dateDatabase = Date(dailyForecastData.dt * 1000) // Convert seconds to milliseconds
                    Log.d("AlarmViewModel", "Date Database: ${dateFormat.format(dateDatabase)} $date")
                    // Check for an exact date match
                    if (date==dateFormat.format(dateDatabase)) {
                        // Create the AlarmDataHolder if a match is found
                        alarmDataHolder = _alarmType.value?.let {
                            AlarmDataHolder(
                                icon = dailyForecastData.weather[0].icon,
                                description = dailyForecastData.weather[0].description,
                                maxTemp = dailyForecastData.temp.max.toString(),
                                minTemp = dailyForecastData.temp.min.toString(),
                                type = it
                            )
                        }
                    }
                }
            }
        }

        // Update the value of _alarmsDetaHolder with the matching AlarmDataHolder or null if not found
        _alarmsDetaHolder.value = alarmDataHolder
        Log.d("AlarmViewModel", "AlarmDataHolder: $alarmDataHolder")
    }




    // New method to get date limits for date picker
    fun getDateLimits(): Pair<Date, Date>? {
        val today = Date() // Current date

        // Get the last date from the _dateStateFlow
        val formattedDates = _dateStateFlow.value
        if (formattedDates.isEmpty()) return null

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val lastDate = dateFormat.parse(formattedDates.last()) ?: return null // Last date in the list

        return Pair(today, lastDate) // Return as a pair of min and max dates
    }



    fun getCurrentWeatherData() {
        viewModelScope.launch {
            repository.getAllWeatherLocalData().collect { weatherData ->
                _currentWeather.value = weatherData
                updateWeatherData(weatherData)
            }
        }

    }
}

