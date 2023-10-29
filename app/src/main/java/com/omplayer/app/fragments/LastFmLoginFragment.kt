package com.omplayer.app.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.omplayer.app.databinding.FragmentLastFmLoginBinding
import com.omplayer.app.viewmodels.LastFmLoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LastFmLoginFragment : BaseMvvmFragment<FragmentLastFmLoginBinding>(FragmentLastFmLoginBinding::inflate) {

    override val viewModel: LastFmLoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            btnLogin.setOnClickListener {
                viewModel.login(
                    username = fUsername.text.toString(),
                    password = fPassword.text.toString(),
                    context = requireContext()
                )
            }
            btnRegister.setOnClickListener { viewModel.register(requireContext()) }
        }
    }
}