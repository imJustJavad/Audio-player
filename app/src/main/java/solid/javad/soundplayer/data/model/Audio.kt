package solid.javad.soundplayer.data.model

import android.graphics.Bitmap
import android.net.Uri

data class Audio(
    val name: String,
    val albumArtist: String,
    val albumArt: Bitmap?,
    val uri: Uri,
    val duration: Int
)
