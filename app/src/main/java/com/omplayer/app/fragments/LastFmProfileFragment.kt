package com.omplayer.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import coil.compose.AsyncImage
import com.omplayer.app.R
import com.omplayer.app.databinding.FragmentLastFmProfileBinding
import com.omplayer.app.theme.OMPlayerTheme
import com.omplayer.app.viewmodels.LastFmProfileViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LastFmProfileFragment: BaseMvvmFragment<FragmentLastFmProfileBinding>(FragmentLastFmProfileBinding::inflate) {

    override val viewModel: LastFmProfileViewModel by viewModels()

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
                        LastFmProfileScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getUserInfo(requireContext())
    }

    @Composable
    fun LastFmProfileScreen(viewModel: LastFmProfileViewModel) {
        val userInfo by viewModel.userInfo.observeAsState()
        val isScrobblingEnabled by viewModel.isScrobblingEnabled.observeAsState()

        LastFmProfileUI(
            username = viewModel.username ?: "",
            userAvatarUrl = userInfo?.images?.firstOrNull { it.size == "extralarge" }?.url ?: "",
            isScrobblingEnabled = isScrobblingEnabled ?: false,
            onScrobblingStateChanged = { viewModel.changeScrobblingState(it) },
            onLogout = { viewModel.logout() },
            onBackPressed = { viewModel.onBackPressed() }
        )
    }

    @Composable
    fun LastFmProfileUI(
        username: String,
        userAvatarUrl: String,
        isScrobblingEnabled: Boolean,
        onScrobblingStateChanged: (Boolean) -> Unit,
        onLogout: () -> Unit,
        onBackPressed: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorResource(id = R.color.colorBackground)
        ) {
            Column(modifier = Modifier.padding(top = 24.dp, bottom = 24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onBackPressed() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            contentDescription = null
                        )
                    }

                    IconButton(
                        onClick = { onLogout() },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_logout),
                            contentDescription = null
                        )
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(top = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = username, style = MaterialTheme.typography.h5.copy(
                            fontSize = 32.sp,
                            color = colorResource(id = R.color.colorTextPrimary),
                            fontWeight = FontWeight.Bold
                        ), modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(bottom = 24.dp)
                    )

                    AsyncImage(
                        model = userAvatarUrl,
                        placeholder = painterResource(id = R.drawable.ic_last_fm_placeholder),
                        error = painterResource(id = R.drawable.ic_last_fm_placeholder),
                        modifier = Modifier
                            .height(150.dp)
                            .width(150.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Fit,
                        contentDescription = null
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 48.dp, start = 16.dp, end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(id = R.string.scrobbling),
                        style = MaterialTheme.typography.h5.copy(
                            fontSize = 14.sp,
                            color = colorResource(id = R.color.colorTextPrimary)
                        ),
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )

                    Switch(
                        checked = isScrobblingEnabled,
                        onCheckedChange = { onScrobblingStateChanged(it) },
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 16.dp),
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = colorResource(id = R.color.colorTextPrimary),
                            checkedTrackColor = colorResource(id = R.color.colorTextPrimary),
                            checkedTrackAlpha = 0.8f,
                            uncheckedThumbColor = colorResource(id = R.color.colorTextPrimary),
                            uncheckedTrackColor = colorResource(id = R.color.colorTextSecondary),
                            uncheckedTrackAlpha = 0.5f
                        )
                    )

                }
            }
        }
    }

    @Preview
    @Composable
    fun PreviewLastFmProfileScreen() {
        OMPlayerTheme {
            LastFmProfileUI(
                username = "Username",
                userAvatarUrl = "https://lastfm.freetls.fastly.net/i/u/300x300/2a96cbd8b46e442fc41c2b86b821562f.png",
                isScrobblingEnabled = true,
                onScrobblingStateChanged = {},
                onLogout = {},
                onBackPressed = {}
            )
        }
    }
}