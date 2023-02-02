package com.omplayer.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.viewmodels.BaseViewModel

abstract class BaseMvvmFragment<T : ViewBinding>(bindingInflater: (layoutInflater: LayoutInflater) -> T): BaseFragment<T>(bindingInflater) {

    protected abstract val viewModel: BaseViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.event.observe(viewLifecycleOwner) { handleEvent(it) }
    }

    private fun handleEvent(event: ViewEvent) {
        if (!(activity as MainActivity).handleBaseEvent(event)) {
            handleCustomEvent(event)
        }
    }

    open fun handleCustomEvent(event: ViewEvent): Boolean = false
}