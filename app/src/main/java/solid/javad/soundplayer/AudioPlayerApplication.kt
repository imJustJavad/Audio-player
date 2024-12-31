package solid.javad.soundplayer

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import solid.javad.soundplayer.data.di.dataSourcesModule
import solid.javad.soundplayer.data.di.repositoriesModule
import solid.javad.soundplayer.ui.di.viewmodelModules

class AudioPlayerApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@AudioPlayerApplication)
            modules(dataSourcesModule, repositoriesModule, viewmodelModules)
        }
    }
}