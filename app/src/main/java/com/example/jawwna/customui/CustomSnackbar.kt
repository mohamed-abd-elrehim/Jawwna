package com.example.jawwna.customui

import android.view.View
import com.google.android.material.snackbar.Snackbar
import androidx.core.content.ContextCompat
import com.example.jawwna.R

class CustomSnackbar {
    companion object {
        fun show(
            view: View,
            message: String,
            isDarkTheme: Boolean,
            buttonText: String = "OK",
            duration: Int = Snackbar.LENGTH_LONG,
            action: (() -> Unit)? = null,
            showButton: Boolean = true // New parameter to control button visibility
        ) {
            val snackbar = Snackbar.make(view, message, duration)

            // Customize theme (Dark/Light)
            val backgroundDrawable = if (isDarkTheme)
                R.drawable.card_settings_field_background_night_mode // Use an appropriate drawable for the dark theme
            else
                R.drawable.card_settings_field_background_light_mode // Use an appropriate drawable for the light theme

            val colorResId = if (isDarkTheme) R.color.colorTextNightMode else R.color.colorText

            snackbar.view.setBackgroundResource(backgroundDrawable)

            // Set action button only if showButton is true
            if (showButton) {
                snackbar.setAction(buttonText) {
                    action?.invoke()
                }
                snackbar.setActionTextColor(ContextCompat.getColor(view.context, colorResId))
            }

            // Animation (Fade in/out effect)
            snackbar.view.alpha = 0f
            snackbar.view.animate().alpha(1f).duration = 300

            // Show the Snackbar
            snackbar.show()
        }
    }
}
