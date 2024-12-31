package solid.javad.soundplayer.data.di

import org.koin.dsl.module
import solid.javad.soundplayer.data.datasource.AudiosLocalDataSource
import solid.javad.soundplayer.data.repositories.AudiosLocalRepository
import solid.javad.soundplayer.data.repositories.impl.AudiosLocalRepositoryImpl

val dataSourcesModule = module {
    single { AudiosLocalDataSource(get()) }
}

val repositoriesModule = module {
    single<AudiosLocalRepository> { AudiosLocalRepositoryImpl(get()) }
}