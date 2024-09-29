package com.example.jawwna.alarmfragment.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.jawwna.datasource.repository.IRepository
import com.example.jawwna.mapscreen.geocodingservice.IGeocodingService

class AlarmViewModelFactory(private val repository: IRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
