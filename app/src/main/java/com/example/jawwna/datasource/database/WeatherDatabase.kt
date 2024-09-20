package com.example.jawwna.datasource.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.FavoriteLocation
import com.example.jawwna.datasource.model.WeatherCondition
import com.example.jawwna.datasource.model.WeatherConverter

// Define the database version and the list of entities
@Database(entities = [CurrentWeather::class, FavoriteLocation::class, WeatherCondition::class], version = 1 )//exportSchema = false)
@TypeConverters(WeatherConverter::class) // Include the WeatherConverter for handling List<Weather>
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
