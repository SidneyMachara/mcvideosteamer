package com.example.mcvideostreamer

// import io.github.thibaultbee.streampack.utils.MediaFormat
import Configuration
import android.os.Bundle
import android.util.Size
import android.view.SurfaceView
import io.flutter.embedding.android.FlutterActivity
import io.flutter.plugin.common.MethodChannel
import io.github.thibaultbee.streampack.data.AudioConfig
import io.github.thibaultbee.streampack.data.VideoConfig
import io.github.thibaultbee.streampack.ext.srt.data.SrtConnectionDescriptor
import io.github.thibaultbee.streampack.ext.srt.streamers.CameraSrtLiveStreamer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : FlutterActivity() {
    private val CHANNEL = "srt_streaming_channel"
    private lateinit var streamer: CameraSrtLiveStreamer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        MethodChannel(flutterEngine!!.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call,
                result ->
            when (call.method) {
                "startStream" -> {
                    try {
                        val ip = call.argument<String>("ip")
                        val port = call.argument<Int>("port")

                        startSrtStream(ip!!, port!!)
                        result.success("Stream started")
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }
                "stopStream" -> {
                    stopSrtStream()
                    result.success("Stream stopped")
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun startSrtStream(ip: String, port: Int) {
        try {

            println(ip)
            println(port)
            streamer = CameraSrtLiveStreamer(this)

            val helper = streamer.helper

            println(helper.audio.supportedEncoders)
            println("bitrate" + helper.audio.getSupportedBitrates("audio/mp4a-latm"))
            println("rate " + helper.audio.getSupportedSampleRates("audio/mp4a-latm"))
            println("chanel" + helper.audio.getSupportedInputChannelRange("audio/mp4a-latm"))
            //

            // println(AudioConfig.)
            // 128000

            val config = Configuration()
            val audioConfig =
                    AudioConfig(
                            startBitrate = config.audio.bitrate,
                            enableEchoCanceler = config.audio.enableEchoCanceler,
                            enableNoiseSuppressor = config.audio.enableNoiseSuppressor,
                    )

            val videoConfig =
                    VideoConfig(
                            startBitrate = 2000000, // 2 Mbps
                            resolution = Size(1280, 720),
                            fps = 30
                    )

            streamer.configure(audioConfig, videoConfig)

            CoroutineScope(Dispatchers.IO).launch {
                streamer.startPreview(findViewById<SurfaceView>(R.id.camera_preview))
                val connection = SrtConnectionDescriptor(ip, port)
                streamer.startStream(connection)
            }
            println('3')
        } catch (e: Exception) {

            // println("----> " + e.message)
            throw e
        }
    }

    private fun stopSrtStream() {
        CoroutineScope(Dispatchers.IO).launch { streamer.stopStream() }

        streamer.disconnect()
        streamer.stopPreview()
        streamer.release()
    }
}
