import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.util.Size

class Configuration() {

    val video = Video()
    val audio = Audio()

    class Video() {
        var enable: Boolean = true

        var encoder: String = MediaFormat.MIMETYPE_VIDEO_AVC

        var fps: Int = 30

        var resolution: Size = Size(1280, 720)

        var bitrate: Int = 2000

        var profile: Int = MediaCodecInfo.CodecProfileLevel.AVCProfileBaseline

        var level: Int = MediaCodecInfo.CodecProfileLevel.AVCLevel1
    }

    class Audio() {
        var enable: Boolean = true

        var encoder: String = MediaFormat.MIMETYPE_AUDIO_AAC

        var numberOfChannels: Int = 6

        var bitrate: Int = 8000

        var sampleRate: Int = 44100

        val byteFormat: Int = 1

        var enableEchoCanceler: Boolean = false

        var enableNoiseSuppressor: Boolean = false

        var profile: Int = MediaCodecInfo.CodecProfileLevel.AACObjectLC
        // var profile: Int = MediaCodecInfo.CodecProfileLevel.HEVCProfileMain10
    }
}
