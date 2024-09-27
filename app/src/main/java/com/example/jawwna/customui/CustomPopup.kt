package com.example.jawwna.customui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import androidx.compose.ui.graphics.Color
import com.example.jawwna.R

class CustomPopup(private val context: Context) {
    private lateinit var popupWindow: PopupWindow

    @SuppressLint("MissingInflatedId")
    fun showPopup(view: View, message: String, isDarkTheme: Boolean) {
        // Inflate the custom layout
        val inflater = LayoutInflater.from(context)
        val popupView = inflater.inflate(R.layout.custom_popup_layout, null)

        // Customize message
        val textView = popupView.findViewById<TextView>(R.id.popup_message)
        textView.text = message

        // Set theme
        val backgroundDrawable = if (isDarkTheme)
            R.drawable.card_settings_field_background_night_mode // Use an appropriate drawable for the dark theme
        else
            R.drawable.card_settings_field_background_light_mode // Use an appropriate drawable for the light theme
        popupView.findViewById<View>(R.id.popup_layout).setBackgroundResource(backgroundDrawable)

        // Create the dialog
        // Initialize PopupWindow
        popupWindow = PopupWindow(popupView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        popupWindow.showAsDropDown(view)
        popupWindow.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))


        // Apply Animation (Fade in)
        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 500
        popupView.startAnimation(fadeIn)

        // Button action (Dismiss)
        val dismissButton = popupView.findViewById<Button>(R.id.dismiss_button)
        dismissButton.setOnClickListener {
            // Apply fade-out animation before dismiss
            val fadeOut = AlphaAnimation(1.0f, 0.0f)
            fadeOut.duration = 500
            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationEnd(animation: Animation?) {
                    popupWindow.dismiss()
                }

                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}
            })
            popupView.startAnimation(fadeOut)
        }
    }
}
