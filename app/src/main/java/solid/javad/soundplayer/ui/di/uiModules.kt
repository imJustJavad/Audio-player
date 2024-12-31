package solid.javad.soundplayer.ui.di

import org.koin.dsl.module
import solid.javad.soundplayer.ui.viewmodel.AudiosScreenViewModel

val viewmodelModules = module {
    single { AudiosScreenViewModel(get(), get(), true) }
}