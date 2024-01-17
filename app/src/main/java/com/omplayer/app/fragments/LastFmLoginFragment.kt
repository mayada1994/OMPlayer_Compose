package com.omplayer.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import com.omplayer.app.R
import com.omplayer.app.databinding.FragmentLastFmLoginBinding
import com.omplayer.app.theme.OMPlayerTheme
import com.omplayer.app.viewmodels.LastFmLoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LastFmLoginFragment :
    BaseMvvmFragment<FragmentLastFmLoginBinding>(FragmentLastFmLoginBinding::inflate) {

    override val viewModel: LastFmLoginViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return super.onCreateView(inflater, container, savedInstanceState).apply {
            binding.composeView.apply {
                // Dispose the Composition when the view's LifecycleOwner is destroyed
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )

                setContent {
                    OMPlayerTheme {
                        LastFmLoginScreen(viewModel)
                    }
                }
            }
        }
    }

    @Composable
    fun LastFmLoginScreen(viewModel: LastFmLoginViewModel) {
        var username by rememberSaveable { mutableStateOf("") }
        var password by rememberSaveable { mutableStateOf("") }
        LastFmLoginUI(
            username = username,
            password = password,
            onBackPressed = { viewModel.onBackPressed() },
            onLoginClick = {
                viewModel.login(
                    username = username,
                    password = password,
                    context = requireContext()
                )
            },
            onRegisterClick = { viewModel.register(requireContext()) },
            onUsernameChanged = { username = it },
            onPasswordChanged = { password = it }
        )
    }

    @Composable
    fun LastFmLoginUI(
        username: String = "",
        password: String = "",
        onBackPressed: () -> Unit,
        onLoginClick: () -> Unit,
        onRegisterClick: () -> Unit,
        onUsernameChanged: (String) -> Unit,
        onPasswordChanged: (String) -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorResource(id = R.color.colorBackground)
        ) {
            Box {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    IconButton(
                        onClick = { onBackPressed() },
                        modifier = Modifier.align(Alignment.Start).padding(top = 16.dp)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.ic_lastfm_logo),
                        modifier = Modifier
                            .height(200.dp)
                            .width(200.dp),
                        colorFilter = ColorFilter.tint(color = colorResource(id = R.color.fire_brick)),
                        contentDescription = null
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedTextField(
                        value = username,
                        onValueChange = { onUsernameChanged(it) },
                        label = {
                            Text(
                                text = stringResource(id = R.string.username),
                                color = colorResource(id = R.color.colorTextSecondary)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = colorResource(id = R.color.transparent),
                            focusedIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                            unfocusedIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                            disabledIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                            textColor = colorResource(id = R.color.colorTextPrimary),
                            cursorColor = colorResource(id = R.color.colorTextPrimary)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    var isPasswordVisible: Boolean by rememberSaveable { mutableStateOf(false) }
                    OutlinedTextField(
                        value = password,
                        onValueChange = { onPasswordChanged(it) },
                        label = {
                            Text(
                                text = stringResource(id = R.string.password),
                                color = colorResource(id = R.color.colorTextSecondary)
                            )
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = {
                                isPasswordVisible = !isPasswordVisible
                            }) {
                                Icon(
                                    painterResource(id = if (isPasswordVisible) R.drawable.ic_visibility_off else R.drawable.ic_visibility),
                                    tint = colorResource(id = R.color.colorTextSecondary),
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.textFieldColors(
                            backgroundColor = colorResource(id = R.color.transparent),
                            focusedIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                            unfocusedIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                            disabledIndicatorColor = colorResource(id = R.color.colorTextSecondary),
                            textColor = colorResource(id = R.color.colorTextPrimary),
                            cursorColor = colorResource(id = R.color.colorTextPrimary)
                        )
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { onLoginClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 48.dp),
                            colors = ButtonDefaults.buttonColors(
                                backgroundColor = colorResource(id = R.color.fire_brick),
                                contentColor = colorResource(id = R.color.colorTextPrimary)
                            ),
                            shape = RoundedCornerShape(40.dp)
                        ) {
                            Text(
                                text = stringResource(id = R.string.login).uppercase(),
                                modifier = Modifier.padding(vertical = 10.dp),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        TextButton(
                            onClick = { onRegisterClick() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 48.dp),
                        ) {
                            Text(
                                text = stringResource(id = R.string.do_not_have_account),
                                color = colorResource(id = R.color.colorTextSecondary)
                            )
                            Text(
                                text = stringResource(id = R.string.register),
                                color = colorResource(id = R.color.colorTextSecondary),
                                modifier = Modifier.padding(start = 4.dp),
                                textDecoration = TextDecoration.Underline
                            )
                        }
                    }
                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewLastLoginScreen() {
        OMPlayerTheme {
            LastFmLoginUI(
                username = "username",
                password = "password",
                onBackPressed = {},
                onLoginClick = {},
                onRegisterClick = {},
                onUsernameChanged = {},
                onPasswordChanged = {}
            )
        }
    }
}