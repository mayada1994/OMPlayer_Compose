package com.omplayer.app.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import com.omplayer.app.events.SingleLiveEvent
import com.omplayer.app.events.ViewEvent

abstract class BaseViewModel: ViewModel() {

    protected val _event = SingleLiveEvent<ViewEvent>()
    val event: LiveData<ViewEvent> = _event

    sealed class BaseViewEvent {
        data class Navigate(val navDirections: NavDirections) : ViewEvent
    }

}