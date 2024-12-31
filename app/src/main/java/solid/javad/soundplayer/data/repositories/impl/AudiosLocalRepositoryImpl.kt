package solid.javad.soundplayer.data.repositories.impl

import solid.javad.soundplayer.data.datasource.AudiosLocalDataSource
import solid.javad.soundplayer.data.model.Audio
import solid.javad.soundplayer.data.repositories.AudiosLocalRepository

class AudiosLocalRepositoryImpl(
    private val dataSource: AudiosLocalDataSource
): AudiosLocalRepository {
    override fun getAudios(): List<Audio> {
        return dataSource.getAudios()
    }
}