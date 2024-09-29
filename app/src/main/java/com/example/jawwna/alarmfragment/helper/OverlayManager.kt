package com.example.jawwna.alarmfragment.helper

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.jawwna.R
import com.example.jawwna.databinding.OverlayLayoutBinding

class OverlayManager(private val context: Context) {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private lateinit var binding: OverlayLayoutBinding

    init {
        windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    fun showOverlay(iconName: String, description: String, minTemp: String, maxTemp: String) {
        if (overlayView != null) return // Prevent multiple overlays

        val inflater = LayoutInflater.from(context)
        overlayView = inflater.inflate(R.layout.overlay_layout, null)
        binding = OverlayLayoutBinding.bind(overlayView!!)

        // Build the full URL for the weather icon
        val modeSuffix = if (isDarkModeEnabled(context)) "n" else "d"
        val baseUrl = "https://openweathermap.org/img/wn/"
        val fullIconUrl = "$baseUrl${iconName.dropLast(1)}$modeSuffix@2x.png"

        // Load the weather icon from the internet using Glide
        Glide.with(context)
            .load(fullIconUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
//            .error(R.drawable.ic_error_placeholder) // Fallback placeholder
            .into(binding.weatherIcon)

        // Set the min and max temperature
        binding.temperatureText.text = "Min: $minTemp, Max: $maxTemp"

        // Set the weather description
        binding.weatherDescription.text = description

        // Set up the layout parameters for the overlay
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.CENTER
        }

        // Add the overlay view to the WindowManager
        windowManager?.addView(overlayView, params)

        // Handle the Dismiss button click
        binding.dismissButton.setOnClickListener {
            dismissOverlay()
        }
    }

    fun dismissOverlay() {
        overlayView?.let {
            windowManager?.removeView(it)
            overlayView = null
        }
    }

    private fun isDarkModeEnabled(context: Context): Boolean {
        val nightModeFlags = context.resources.configuration.uiMode and android.content.res.Configuration.UI_MODE_NIGHT_MASK
        return nightModeFlags == android.content.res.Configuration.UI_MODE_NIGHT_YES
    }
}
