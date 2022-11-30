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

    /**
     * Returns true if the event is base and false if it is custom
     */
    private fun handleEvent(event: ViewEvent): Boolean {
        return if (BaseViewModel.BaseViewEvent::class.nestedClasses.contains(event::class)) {
            (activity as MainActivity).handleBaseEvent(event)
            true
        } else {
            false
        }
    }
}