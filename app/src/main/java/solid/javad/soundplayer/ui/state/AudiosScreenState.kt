package solid.javad.soundplayer.ui.state

import solid.javad.soundplayer.data.model.Audio

sealed class AudiosScreenState {
    data object Loading: AudiosScreenState()
    data class Error(val message: String): AudiosScreenState()
    data class Successful (
        val permissionIsGranted: Boolean = true,
        val audios: List<Audio> = emptyList(),
        val playerState: PlayerState = PlayerState()
    ): AudiosScreenState()
}

data class PlayerState (
    val currentAudio: Int? = null,
    val duration: Int? = null,
    val currentSecond: Int? = null
)