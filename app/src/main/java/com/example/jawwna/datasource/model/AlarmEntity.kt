package com.example.jawwna.datasource.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "alarm",
    primaryKeys =["date", "time"] // Ensure unique combination of date and time
)
data class AlarmEntity(
 val date: String, // Date as part of the composite key
 val time: String   // Time as part of the composite key
)

