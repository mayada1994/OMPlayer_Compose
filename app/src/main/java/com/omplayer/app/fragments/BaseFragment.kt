package com.omplayer.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.omplayer.app.activities.MainActivity
import com.omplayer.app.events.ViewEvent
import com.omplayer.app.viewmodels.BaseViewModel

abstract class BaseFragment<T : ViewBinding>(private val bindingInflater: (layoutInflater: LayoutInflater) -> T): Fragment() {

    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected abstract val viewModel: BaseViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return bindingInflater.invoke(inflater).also { _binding = it }.root
    }

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}