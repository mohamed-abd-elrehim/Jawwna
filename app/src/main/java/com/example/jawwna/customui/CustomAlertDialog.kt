package com.example.jawwna.customui
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Button
import android.view.animation.AlphaAnimation
import androidx.core.content.ContextCompat
import com.example.jawwna.R
class CustomAlertDialog(private val context: Context) {
    @SuppressLint("MissingInflatedId")
    fun showDialog(
        title: String? = null,
        message: String,
        isDarkTheme: Boolean,
        positiveText: String = context.getString(R.string.ok), // Default text for positive button
        negativeText: String = context.getString(R.string.cancel), // Default text for negative button
        positiveAction: (() -> Unit)? = null,
        negativeAction: (() -> Unit)? = null
    ) {
        val builder = AlertDialog.Builder(context)

        // Inflate custom layout for the AlertDialog
        val inflater = LayoutInflater.from(context)
        val dialogView: View = inflater.inflate(R.layout.custom_alert_layout, null)
        builder.setView(dialogView)

        // Set message
        val textViewMessage = dialogView.findViewById<TextView>(R.id.alert_message)
        val textViewTitle = dialogView.findViewById<TextView>(R.id.alert_title)
        textViewMessage.text = message
        textViewTitle.text = title

        // Set theme
        val backgroundDrawable = if (isDarkTheme)
            R.drawable.card_settings_field_background_night_mode // Use an appropriate drawable for the dark theme
        else
            R.drawable.card_settings_field_background_light_mode // Use an appropriate drawable for the light theme
        dialogView.findViewById<View>(R.id.alert_layout).setBackgroundResource(backgroundDrawable)

        // Create the dialog
        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Buttons
        val positiveButton = dialogView.findViewById<Button>(R.id.positive_button)
        val negativeButton = dialogView.findViewById<Button>(R.id.negative_button)

        // Set button texts
        positiveButton.text = positiveText
        negativeButton.text = negativeText

        // Set text color based on theme
        val colorResId = if (isDarkTheme) R.color.colorTextNightMode else R.color.colorText
        positiveButton.setTextColor(ContextCompat.getColor(context, colorResId))
        negativeButton.setTextColor(ContextCompat.getColor(context, colorResId))

        positiveButton.setOnClickListener {
            positiveAction?.invoke()
            dialog.dismiss()
        }

        negativeButton.setOnClickListener {
            negativeAction?.invoke()
            dialog.dismiss()
        }

        // Animation (Fade in when the dialog appears)
        dialog.setOnShowListener {
            val fadeIn = AlphaAnimation(0.0f, 1.0f)
            fadeIn.duration = 500
            dialog.window?.decorView?.startAnimation(fadeIn)
        }

        // Show dialog
        dialog.show()
    }
}
