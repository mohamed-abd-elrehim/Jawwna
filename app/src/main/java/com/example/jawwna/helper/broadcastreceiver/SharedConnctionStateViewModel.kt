package com.example.jawwna.helper.broadcastreceiver

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class SharedConnctionStateViewModel : ViewModel() {
    private val _sharedConnctionState = MutableSharedFlow<Boolean>()
    val sharedConnctionState: SharedFlow<Boolean> get() = _sharedConnctionState

    suspend fun updateSharedData(isConnected: Boolean) {
        _sharedConnctionState.emit(isConnected)
    }
}
