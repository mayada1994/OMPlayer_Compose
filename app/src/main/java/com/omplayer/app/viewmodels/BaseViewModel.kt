package com.omplayer.app.viewmodels

import android.widget.Toast
import androidx.annotation.StringRes
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
        data class ShowError(@StringRes val resId: Int, val duration: Int = Toast.LENGTH_SHORT) : ViewEvent
    }

}