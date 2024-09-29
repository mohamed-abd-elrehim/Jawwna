package com.example.jawwna.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jawwna.datasource.model.AlarmEntity
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.FavoriteLocation
import com.example.jawwna.datasource.model.WeatherCondition
import com.example.jawwna.datasource.model.WeatherConditionConverter
import com.example.jawwna.datasource.model.WeatherDataConverter
import com.example.jawwna.datasource.model.WeatherListConverter
import com.example.jawwna.datasource.model.FavoriteWeatherEntity
import com.example.jawwna.datasource.model.ForcastDailyDataConverter
import com.example.jawwna.datasource.model.ForcastHourlyDataConverter
import com.example.jawwna.datasource.model.WeatherResponseEntity

// Define the database version and the list of entities
@Database(entities = [FavoriteWeatherEntity::class, WeatherResponseEntity::class,CurrentWeather::class, FavoriteLocation::class, WeatherCondition::class, AlarmEntity::class], version = 1 )//exportSchema = false)
@TypeConverters(WeatherDataConverter::class , ForcastDailyDataConverter::class, ForcastHourlyDataConverter::class,WeatherConditionConverter::class , WeatherDataConverter::class, WeatherListConverter::class) // Include the WeatherConverter for handling List<Weather>
abstract class WeatherDatabase : RoomDatabase() {

    // Define abstract DAOs to be implemented elsewhere
    abstract fun currentWeatherDao(): WeatherDAO

    companion object {
        // The singleton instance of the database
        @Volatile
        private var INSTANCE: WeatherDatabase? = null

        // Method to get the instance of the database
        fun getDatabase(context: Context): WeatherDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    WeatherDatabase::class.java,
                    "weather_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
