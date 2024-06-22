package com.grid.gridlicense

import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityScopedViewModel @Inject constructor() : ViewModel() {
    private val _mainActivityEvent = Channel<ActivityScopedUIEvent>()
    val mainActivityEvent = _mainActivityEvent.receiveAsFlow()

    fun finish() {
        viewModelScope.launch {
            _mainActivityEvent.send(ActivityScopedUIEvent.Finish)
        }
    }

    fun openAppStorageSettings() {
        viewModelScope.launch {
            _mainActivityEvent.send(ActivityScopedUIEvent.OpenAppSettings)
        }
    }

    fun startChooserActivity(
            intent: Intent
    ) {
        viewModelScope.launch {
            _mainActivityEvent.send(ActivityScopedUIEvent.StartChooserActivity(intent))
        }
    }

}