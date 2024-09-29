package com.example.jawwna.mapscreen.geocodingservice

import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.util.Locale

class GeocodingService(private val context: Context) : IGeocodingService {
    private val TAG = "GeocodingService"

    override fun searchPlace(query: String): Flow<LatLng?> = flow {
        val geocoder = Geocoder(context)
        try {
            val addresses: List<Address>? = geocoder.getFromLocationName(query, 1)

            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                val latLng = LatLng(address.latitude, address.longitude)
                emit(latLng) // Emit the location
            } else {
                Log.e(TAG, "No addresses found for the query: $query")
                emit(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Geocoding failed: ${e.message}", e)
            emit(null) // Handle exception
        }
    }

    override  fun getCountryNameFromLatLong(latitude: Double, longitude: Double): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        return try {
            // Fetch the address list using the geocoder
            val addresses: MutableList<Address>? = geocoder.getFromLocation(latitude, longitude, 1)

            // If the address list is not empty, return the country name
            if (!addresses.isNullOrEmpty()) {
               // val city = addresses[0].locality // اسم المدينة أو المنطقة
                val province = addresses[0].adminArea // اسم المحافظة
                return " $province" // دم

            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}


