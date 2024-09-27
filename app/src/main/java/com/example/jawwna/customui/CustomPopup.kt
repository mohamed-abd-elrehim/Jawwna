package com.example.jawwna.customui

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.Gravity
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
    private lateinit var popupWindow: PopupWindow

    @SuppressLint("MissingInflatedId")
    class CustomPopup(private val context: Context) {
        private lateinit var popupWindow: PopupWindow

        fun showPopup(view: View, title: String?, message: String, isDarkTheme: Boolean) {
            // Inflate the custom layout
            val inflater = LayoutInflater.from(context)
            val popupView = inflater.inflate(R.layout.custom_popup_layout, null)

            // Customize message
            val textView = popupView.findViewById<TextView>(R.id.popup_message)
            textView.text = message

            val titel = popupView.findViewById<TextView>(R.id.popup_titel)
            titel.text = title

            // Set theme
            val backgroundDrawable = if (isDarkTheme)
                R.drawable.card_settings_field_background_night_mode
            else
                R.drawable.card_settings_field_background_light_mode
            popupView.findViewById<View>(R.id.popup_layout).setBackgroundResource(backgroundDrawable)

            // Initialize PopupWindow
            popupWindow = PopupWindow(
                popupView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                true
            )


            // Set background to transparent
            popupWindow.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

            // Display the popup in the center of the screen
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0)

            // Apply Animation (Fade in)
            val fadeIn = AlphaAnimation(0.0f, 1.0f)
            fadeIn.duration = 500
            popupView.startAnimation(fadeIn)

            // Button action (Dismiss)
            val dismissButton = popupView.findViewById<Button>(R.id.dismiss_button)
            dismissButton.setOnClickListener {
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


