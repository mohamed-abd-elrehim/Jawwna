package com.example.jawwna.datasource.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "alarm",
    primaryKeys = ["date", "time"] // Ensure unique combination of date and time
)
data class AlarmEntity(
    val date: String, // Date as part of the composite key
    val time: String,   // Time as part of the composite ke
    val isActive: Boolean = true,
    val icon: String,
    val maxTemp: String,
    val minTemp: String,
    val description: String,
    val type: String
)

