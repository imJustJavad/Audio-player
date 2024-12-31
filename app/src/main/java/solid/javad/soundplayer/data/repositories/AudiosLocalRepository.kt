package solid.javad.soundplayer.data.repositories

import solid.javad.soundplayer.data.model.Audio

interface AudiosLocalRepository {
    fun getAudios(): List<Audio>
}