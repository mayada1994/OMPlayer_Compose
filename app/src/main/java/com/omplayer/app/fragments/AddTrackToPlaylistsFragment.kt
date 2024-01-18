package com.omplayer.app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.omplayer.app.R
import com.omplayer.app.databinding.DialogAddChangePlaylistBinding
import com.omplayer.app.databinding.FragmentAddTrackToPlaylistsBinding
import com.omplayer.app.db.entities.Playlist
import com.omplayer.app.items.PlaylistItem
import com.omplayer.app.theme.OMPlayerTheme
import com.omplayer.app.viewmodels.AddTrackToPlaylistsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddTrackToPlaylistsFragment : BaseMvvmFragment<FragmentAddTrackToPlaylistsBinding>(FragmentAddTrackToPlaylistsBinding::inflate) {

    override val viewModel: AddTrackToPlaylistsViewModel by viewModels()

    private val args: AddTrackToPlaylistsFragmentArgs by navArgs()

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
                        AddTrackToPlaylistsScreen(viewModel)
                    }
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.init(args.trackId)
    }

    private fun showAddPlaylistDialog() {
        val dialogBinding = DialogAddChangePlaylistBinding.inflate(layoutInflater)
        val alertDialog = MaterialAlertDialogBuilder(requireContext()).setView(dialogBinding.root).create()
        with(dialogBinding) {
            btnSave.setOnClickListener {
                viewModel.addPlaylist(fPlaylistTitle.text.toString())
                alertDialog.dismiss()
            }
            btnCancel.setOnClickListener {
                alertDialog.dismiss()
            }
        }
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    @Composable
    fun AddTrackToPlaylistsScreen(viewModel: AddTrackToPlaylistsViewModel) {
        val playlists by viewModel.playlists.observeAsState()
        val selectedPlaylists by viewModel.selectedPlaylists.observeAsState()

        AddTrackToPlaylistsUI(
            playlists = playlists ?: listOf(),
            selectedPlaylists = selectedPlaylists ?: listOf(),
            onPlaylistSelected = { viewModel.onPlaylistSelected(it) },
            onAddPlaylistClicked = { showAddPlaylistDialog() },
            onSaveClicked = { viewModel.onSaveClicked() },
            onBackPressed = { viewModel.onBackPressed() }
        )
    }

    @Composable
    fun AddTrackToPlaylistsUI(
        playlists: List<Playlist>,
        selectedPlaylists: List<Playlist>,
        onPlaylistSelected: (Playlist) -> Unit,
        onAddPlaylistClicked: () -> Unit,
        onSaveClicked: () -> Unit,
        onBackPressed: () -> Unit
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorResource(id = R.color.colorBackground)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, start = 4.dp, end = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = { onBackPressed() },
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_back),
                            tint = colorResource(id = R.color.colorTextPrimary),
                            contentDescription = null
                        )
                    }

                    Text(
                        text = stringResource(
                            id = R.string.selected_playlists,
                            pluralStringResource(
                                id = R.plurals.playlists_count,
                                count = selectedPlaylists.count(),
                                selectedPlaylists.count()
                            )
                        ),
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                            .align(Alignment.CenterVertically),
                        color = colorResource(id = R.color.colorTextPrimary),
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    IconButton(
                        onClick = { onAddPlaylistClicked() },
                        modifier = Modifier
                            .wrapContentWidth()
                            .wrapContentHeight()
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            tint = colorResource(id = R.color.colorTextPrimary),
                            contentDescription = null
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 36.dp)
            ) {
                if (playlists.isNotEmpty()) {
                    LazyColumn(modifier = Modifier.padding(top = 24.dp),
                        content = {
                            items(playlists) { playlist ->
                                PlaylistItem(title = playlist.title,
                                    songsCount = playlist.tracks.count(),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .clickable {
                                            onPlaylistSelected(playlist)
                                        }
                                        .background(
                                            color = if (selectedPlaylists.contains(playlist)) {
                                                colorResource(id = R.color.colorSelectedBackground)
                                            } else {
                                                colorResource(id = R.color.transparent)
                                            }
                                        ),
                                    onMenuClicked = { })

                                if (playlists.indexOf(playlist) != playlists.lastIndex) {
                                    Divider(
                                        color = colorResource(id = R.color.colorDivider),
                                        thickness = 1.dp
                                    )
                                }
                            }
                        })

                    TextButton(
                        onClick = { onSaveClicked() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter),
                        colors = ButtonDefaults.textButtonColors(
                            backgroundColor = colorResource(id = R.color.colorSelectedBackground)
                        )
                    ) {
                        Text(
                            text = stringResource(id = R.string.save).uppercase(),
                            color = colorResource(id = R.color.colorTextPrimary),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight()
                                .padding(vertical = 8.dp)
                        )
                    }
                } else {
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 32.dp)
                            .align(Alignment.Center),
                        text = stringResource(id = R.string.no_playlists),
                        color = colorResource(id = R.color.colorTextSecondary),
                        fontSize = 36.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Preview
    @Composable
    fun AddTrackToPlaylistsScreenPreview() {
        OMPlayerTheme {
            AddTrackToPlaylistsUI(
                playlists = listOf(
                    Playlist(
                        title = "Favorites 1",
                        tracks = listOf(1, 2, 3)
                    ),
                    Playlist(
                        title = "Favorites 2",
                        tracks = listOf(1, 2, 3)
                    ),
                    Playlist(
                        title = "Favorites 3",
                        tracks = listOf(1, 2, 3)
                    ),
                    Playlist(
                        title = "Favorites 4",
                        tracks = listOf(1, 2, 3)
                    ),
                    Playlist(
                        title = "Favorites 5",
                        tracks = listOf(1, 2, 3)
                    )
                ),
                selectedPlaylists = listOf(
                    Playlist(
                        title = "Favorites 1",
                        tracks = listOf(1, 2, 3)
                    ),
                    Playlist(
                        title = "Favorites 2",
                        tracks = listOf(1, 2, 3)
                    )
                ),
                onPlaylistSelected = {},
                onAddPlaylistClicked = {},
                onSaveClicked = {},
                onBackPressed = {}
            )
        }
    }
}