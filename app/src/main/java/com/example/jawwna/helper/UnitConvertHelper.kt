package com.example.jawwna.helper

import java.math.BigDecimal
import java.math.RoundingMode

object UnitConvertHelper {

    // Temperature conversion based on the TemperatureUnits enum
    fun convertTemperature(value: Double, fromUnit: String?, toUnit: TemperatureUnits): Double {
        return when (toUnit) {
            TemperatureUnits.metric -> roundToOneDecimal(value - 273.15) // Kelvin to Celsius
            TemperatureUnits.imperial -> roundToOneDecimal((value - 273.15) * 9 / 5 + 32) // Kelvin to Fahrenheit
            TemperatureUnits.standard -> roundToOneDecimal(value) // Already in Kelvin

        }
    }

    // Wind speed conversion based on the WindSpeedUnits enum
    fun convertWindSpeed(value: Double, fromUnit: String?, toUnit: WindSpeedUnits): Double {
        return when (toUnit) {
            WindSpeedUnits.metric -> roundToOneDecimal(value) // m/s to km/h
            WindSpeedUnits.imperial -> roundToOneDecimal(value * 2.23694) // m/s to mph
        }
    }

    // Helper function to round a number to one decimal place
    private fun roundToOneDecimal(value: Double): Double {
        //return String.format("%.1f", value).toDouble()
        return BigDecimal(value).setScale(1, RoundingMode.HALF_UP).toDouble()

    }
}
