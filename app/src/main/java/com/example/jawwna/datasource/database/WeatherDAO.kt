package com.example.jawwna.datasource.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.jawwna.datasource.model.CurrentWeather
import com.example.jawwna.datasource.model.FavoriteLocation
import com.example.jawwna.datasource.model.ForecastResponse
import kotlinx.coroutines.flow.Flow

@Dao
interface WeatherDAO {


    // Current location weather operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCurrentWeather(currentWeather: CurrentWeather)

    @Query("SELECT * FROM current_weather WHERE id = :id")
    suspend fun getCurrentWeather(id: Long): CurrentWeather?

    @Query("DELETE FROM current_weather WHERE id = :id")
    suspend fun deleteCurrentWeather(id: Long)

    @Query("DELETE FROM current_weather")
    suspend fun deleteAllCurrentWeather()


    // Favorite locations operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteLocation(favoriteLocation: FavoriteLocation)

    @Query("SELECT * FROM favorite_locations WHERE id = :id")
    suspend fun getFavoriteLocation(id: Long): FavoriteLocation?

    @Query("SELECT * FROM favorite_locations")
     fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>

    @Delete
    suspend fun deleteFavoriteLocation(favoriteLocation: FavoriteLocation)

    @Query("DELETE FROM favorite_locations")
    suspend fun deleteAllFavoriteLocations()




}
