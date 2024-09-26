package com.example.jawwna.add_favorite_location_screen.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.mapscreen.geocodingservice.IGeocodingService

class AddFavoriteLocationViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddFavoriteLocationViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddFavoriteLocationViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
