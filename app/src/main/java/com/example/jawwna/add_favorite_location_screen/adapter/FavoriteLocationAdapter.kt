package com.example.jawwna.homescreen.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Configuration
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.jawwna.R
import com.example.jawwna.databinding.AddFavoritRowBinding
import com.example.jawwna.databinding.ForecastDalyRowBinding
import com.example.jawwna.datasource.model.DailyForecastData
import com.example.jawwna.datasource.model.FavoriteLocation
import com.example.jawwna.datasource.model.FavoriteLocationModel

class FavoriteLocationAdapter(
    private var forecastList: MutableList<FavoriteLocationModel>,
    private val context: Context,
    private val itemClickListener: OnItemClickListener,
    private val deleteItemClickListener: OnDeleteItemClickListener
) : RecyclerView.Adapter<FavoriteLocationAdapter.ForecastViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(forecast: FavoriteLocationModel)
    }
    interface OnDeleteItemClickListener {
        fun onItemClick(forecast: FavoriteLocationModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val binding = DataBindingUtil.inflate<AddFavoritRowBinding>(
            LayoutInflater.from(parent.context),
            R.layout.add_favorit_row,
            parent,
            false
        )
        return ForecastViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val forecast = forecastList[position]
        holder.bind(forecast)
    }

    override fun getItemCount(): Int = forecastList.size

    inner class ForecastViewHolder(private val binding: AddFavoritRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("ResourceAsColor")
        fun bind(forecast: FavoriteLocationModel) {
            binding.favoriteLocationModel = forecast
            binding.executePendingBindings()

            val iconName = forecast.icon ?: return // Ensure iconName is not null
            val modeSuffix = if (isDarkModeEnabled(context)) "n" else "d"
            val baseUrl = "https://openweathermap.org/img/wn/"
            val fullIconUrl = "$baseUrl${iconName.dropLast(1)}$modeSuffix@2x.png"

            Glide.with(context)
                .load(fullIconUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(binding.favoriteLocationImageWeather)

            // Apply dark/light mode backgrounds
            val backgroundResource = if (isDarkModeEnabled(context)) {
                R.drawable.card_settings_field_background_night_mode
            } else {
                R.drawable.card_settings_field_background_light_mode
            }
            binding.favoriteLocationLayout.setBackgroundResource(backgroundResource)
            val textColor = if (isDarkModeEnabled(context)) {
                ContextCompat.getColor(context, android.R.color.holo_blue_bright)
            } else {
                ContextCompat.getColor(context, android.R.color.holo_blue_dark)
            }
            binding.favoriteLocationTextDes.setTextColor(textColor)
            binding.favoriteLocationTextTemperature.setTextColor(textColor)

            itemView.setOnClickListener {
                itemClickListener.onItemClick(forecast)
            }
            binding.deleteFavorite.setOnClickListener {
                deleteItemClickListener.onItemClick(forecast)
            }
        }

        private fun isDarkModeEnabled(context: Context): Boolean {
            val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            return nightModeFlags == Configuration.UI_MODE_NIGHT_YES
        }
    }

    fun updateData(newForecastList: MutableList<FavoriteLocationModel>) {
        forecastList = newForecastList

        notifyDataSetChanged() // Consider using DiffUtil for more efficient updates
    }


}
