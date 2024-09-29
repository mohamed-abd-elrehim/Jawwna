package com.example.jawwna.helper.broadcastreceiver

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
class SharedConnctionStateViewModel : ViewModel() {

    companion object {
        // This shared flow is a singleton, shared across all instancaves of the ViewModel
        private val _sharedConnctionState = MutableSharedFlow<Boolean>(replay = 1)
        val sharedConnctionState: SharedFlow<Boolean> get() = _sharedConnctionState
    }

    suspend fun updateSharedData(isConnected: Boolean) {
        _sharedConnctionState.emit(isConnected)
    }
}
