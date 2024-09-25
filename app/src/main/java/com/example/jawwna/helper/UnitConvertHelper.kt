package com.example.jawwna.helper

import java.math.BigDecimal
import java.math.RoundingMode

object UnitConvertHelper {

    // Temperature conversion based on the TemperatureUnits enum
    fun convertTemperature(value: Double, fromUnit: String?, toUnit: TemperatureUnits): Double {
        return when (fromUnit) {
            TemperatureUnits.metric.toString() -> when (toUnit) {
                TemperatureUnits.metric -> roundToOneDecimal(value)
                TemperatureUnits.imperial -> roundToOneDecimal((value * 9 / 5) + 32)
                TemperatureUnits.standard -> roundToOneDecimal(value + 273.15)
            }
            TemperatureUnits.imperial.toString() -> when (toUnit) {
                TemperatureUnits.metric -> roundToOneDecimal((value - 32) * 5 / 9)
                TemperatureUnits.imperial -> roundToOneDecimal(value)
                TemperatureUnits.standard -> roundToOneDecimal((value - 32) * 5 / 9 + 273.15)
            }
            else -> when (toUnit) {
                TemperatureUnits.metric -> roundToOneDecimal(value - 273.15)
                TemperatureUnits.imperial -> roundToOneDecimal((value - 273.15) * 9 / 5 + 32)
                TemperatureUnits.standard -> roundToOneDecimal(value)
            }
        }
    }

    // Wind speed conversion based on the WindSpeedUnits enum
    fun convertWindSpeed(value: Double, fromUnit: String?, toUnit: WindSpeedUnits): Double {
        return when (fromUnit) {
            WindSpeedUnits.metric.toString() -> when (toUnit) {
                WindSpeedUnits.metric -> roundToOneDecimal(value)
                WindSpeedUnits.imperial -> roundToOneDecimal(value * 2.23694)
            }
            else -> when (toUnit) {
                WindSpeedUnits.metric -> roundToOneDecimal(value / 2.23694)
                WindSpeedUnits.imperial -> roundToOneDecimal(value)
            }
        }
    }

    // Helper function to round a number to one decimal place
    private fun roundToOneDecimal(value: Double): Double {
        //return String.format("%.1f", value).toDouble()
        return BigDecimal(value).setScale(1, RoundingMode.HALF_UP).toDouble()

    }
}
