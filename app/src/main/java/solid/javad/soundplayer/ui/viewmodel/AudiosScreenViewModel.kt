package solid.javad.soundplayer.ui.viewmodel

import android.app.Application
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import solid.javad.soundplayer.data.model.Audio
import solid.javad.soundplayer.data.repositories.AudiosLocalRepository
import solid.javad.soundplayer.ui.state.AudiosScreenState
import solid.javad.soundplayer.ui.state.PlayerState

class AudiosScreenViewModel(
    private val application: Application,
    private val repository: AudiosLocalRepository,
    checkPermission: Boolean
) : AndroidViewModel(application) {
    private val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_AUDIO
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }

    private val isPermissionGrantedState = MutableStateFlow(
        !checkPermission || isPermissionGranted()
    )

    private val currentAudioState = MutableStateFlow<Int?>(null)
    private val audioDurationState = MutableStateFlow<Int?>(null)
    private val currentSecondState = MutableStateFlow<Int?>(null)
    private val playerState = combine (
        currentAudioState,
        audioDurationState,
        currentSecondState
    ) { currentAudio, audioDuration, currentSecond ->
        PlayerState(currentAudio, audioDuration, currentSecond)
    }

    private val audiosState = isPermissionGrantedState.map {
        if (it) repository.getAudios() else emptyList()
    }

    private var player: MediaPlayer? = null

    val state = combine(
        isPermissionGrantedState,
        playerState,
        audiosState
    ) { isPermissionGranted, player, audios ->
        AudiosScreenState.Successful(isPermissionGranted, audios, player)
    }.catch {
        AudiosScreenState.Error(it.message ?: "Unknown Error")
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AudiosScreenState.Loading
    )

    private fun isPermissionGranted(): Boolean {
        return application.applicationContext.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    fun playAudio(index: Int) {
        val audio = repository.getAudios()[index]
        val uri = audio.uri

        releasePlayer()

        player = MediaPlayer().apply {
            setDataSource(application.applicationContext, uri)

            setOnPreparedListener {
                start()

                currentSecondState.value = 0
                audioDurationState.value = duration / 1000
                currentAudioState.value = index

                updateCurrentSecond()
            }
            setOnCompletionListener {
                releasePlayer()
            }
            setOnSeekCompleteListener {
                currentSecondState.value = currentPosition / 1000
                audioDurationState.value = duration / 1000
                currentAudioState.value = index
            }

            prepareAsync()
        }
    }

    fun pauseAudio() {
        player?.takeIf { it.isPlaying }?.also {
            it.pause()
        }

        currentAudioState.value = null
    }

    fun seekTo(second: Int) {
        player?.also {
            it.seekTo(second * 1000)
        }
    }

    private fun updateCurrentSecond() {
        viewModelScope.launch {
            while (player != null) {
                currentSecondState.value = player!!.currentPosition / 1000
                delay(500)
            }
        }
    }

    private fun releasePlayer() {
        player?.release()
        player = null
        currentAudioState.value = null
        audioDurationState.value = null
        currentSecondState.value = null
    }

    @Composable
    fun getLauncher() = rememberLauncherForActivityResult (
        contract = ActivityResultContracts.RequestPermission(),
        onResult = {
            isPermissionGrantedState.value = it
        }
    )

    fun requestPermission(launcher: ManagedActivityResultLauncher<String, Boolean>) {
        launcher.launch(permission)
    }
}
