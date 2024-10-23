package com.example.jawwna.homescreen.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.jawwna.R
import com.example.jawwna.databinding.ForecastDalyRowBinding
import com.example.jawwna.datasource.model.DailyForecastData

class DailyWeatherForecastAdapter(
    private var forecastList: List<DailyForecastData>,
    private val context: Context,
    private val itemClickListener: OnItemClickListener // Add this line
) : RecyclerView.Adapter<DailyWeatherForecastAdapter.ForecastViewHolder>() {

    interface OnItemClickListener { // Define the interface
        fun onItemClick(forecast: DailyForecastData)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = DataBindingUtil.inflate<ForecastDalyRowBinding>(
            LayoutInflater.from(parent.context),
            R.layout.forecast_daly_row,
            parent,
            false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        Log.d("DailyWeatherForecastAdapter", "Binding position: $position, forecast: ${forecastList.size}")

        val forecast = forecastList[position]
        holder.bind(forecast)
        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(forecast)
        }
    }

    override fun getItemCount(): Int = forecastList.size

    inner class ForecastViewHolder(private val binding: ForecastDalyRowBinding) :
        RecyclerView.ViewHolder(binding.root) {


        @SuppressLint("ResourceAsColor")
        fun bind(forecast: DailyForecastData) {
            itemView.setOnClickListener{
                itemClickListener.onItemClick(forecast)
            }
            binding.dailyForecastData = forecast
            binding.executePendingBindings()

            // Check if dark mode is enabled
            val modeSuffix = if (isDarkModeEnabled(context)) "n" else "d"

            // Split the icon name and construct the URL
            val iconName = forecast.icon // Example: "10d" or "01n"
            val baseUrl = "https://openweathermap.org/img/wn/"
            val fullIconUrl = "$baseUrl${iconName?.dropLast(1)}$modeSuffix@2x.png"

            // Load icon using Glide, applying cache strategy
            Glide.with(context)
                .load(fullIconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL) // Cache all versions of the image
                .into(binding.dayForecastImageWeather)

            // Apply dark/light mode backgrounds
            if (isDarkModeEnabled(context)) {
                binding.dayForecastLayout1.setBackgroundResource(R.drawable.card_settings_field_background_night_mode)
                binding.dayForecastTextDay.setTextColor(ContextCompat.getColor(context, android.R.color.white))
                binding.dayForecastTextDes.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_bright))
                binding.dayForecastTextTemperature.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_bright))
            } else {
                binding.dayForecastLayout1.setBackgroundResource(R.drawable.card_settings_field_background_light_mode)
                binding.dayForecastTextDay.setTextColor(ContextCompat.getColor(context, android.R.color.black))
                binding.dayForecastTextDes.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
                binding.dayForecastTextTemperature.setTextColor(ContextCompat.getColor(context, android.R.color.holo_blue_dark))
            }

        }

        // Utility method to check if dark mode is enabled
        private fun isDarkModeEnabled(context: Context): Boolean {
            val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        }
    }

    // Method to update data
    fun updateData(newForecastList: List<DailyForecastData>) {
        Log.d("DailyWeatherForecastAdapter", "updateData: ${newForecastList.size}")
        forecastList = newForecastList
        notifyDataSetChanged()
    }
}
