package com.example.mcvideostreamer

import CameraPreviewViewFactory
import Configuration
import android.graphics.SurfaceTexture
import android.media.MediaFormat
import android.os.Bundle
import android.util.Size
import android.view.Surface
import android.view.TextureView
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
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
    private lateinit var cameraPreviewFactory: CameraPreviewViewFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // setContentView(R.layout.activity_main)
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        cameraPreviewFactory = CameraPreviewViewFactory()
        flutterEngine.platformViewsController.registry.registerViewFactory(
                "camera-preview-view",
                cameraPreviewFactory
        )

        //
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler {
                call,
                result ->
            when (call.method) {
                "initCameraPreview" -> {
                    initCameraPreview()
                }
                "startStream" -> {
                    val ip = call.argument<String>("ip")
                    val port = call.argument<Int>("port")
                    startSrtStream(ip!!, port!!, result)
                }
                "stopStream" -> {
                    stopSrtStream()
                    result.success("Stream stopped")
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun startSrtStream(ip: String, port: Int, result: MethodChannel.Result) {
        println(ip)
        println(port)
        CoroutineScope(Dispatchers.IO).launch {
            val connection = SrtConnectionDescriptor(ip, port)
            streamer.startStream(connection)
        }

        // Return the SurfaceTexture ID to Flutter
        result.success("Sreamming stratedddd")
    }

    private fun initCameraPreview() {
        try {

            streamer = CameraSrtLiveStreamer(this@MainActivity)

            val helper = streamer.helper
            println("====>helper.video.supportedEncoders")
            println(helper.video.supportedEncoders)
            println(helper.video.getSupportedAllProfiles(MediaFormat.MIMETYPE_VIDEO_AVC))

            val config = Configuration()
            val audioConfig =
                    AudioConfig(
                            startBitrate = config.audio.bitrate,
                            enableEchoCanceler = config.audio.enableEchoCanceler,
                            enableNoiseSuppressor = config.audio.enableNoiseSuppressor,
                    )
            // [1, 65536, 524288, 8, 2]
            // profile = 2,
            val videoConfig =
                    VideoConfig(
                            startBitrate = 2000000, // 2 Mbps
                            resolution = Size(1280, 720),
                            fps = 30
                    )

            println("==============>  Done configes")
            streamer.configure(audioConfig, videoConfig)
            println("==============>  streamer.configure(audioConfig, videoConfig)")
            val textureView = cameraPreviewFactory.getCameraPreviewView()!!.view as TextureView
            streamer.startPreview(Surface(textureView.surfaceTexture))
            println("==============>  got textureView")
            textureView.surfaceTextureListener =
                    object : TextureView.SurfaceTextureListener {
                        override fun onSurfaceTextureAvailable(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                        ) {
                            try {
                                println("<========Preview on===========>")
                                streamer.startPreview(Surface(surface))
                                println("<========Preview on on===========>")
                            } catch (e: Exception) {
                                println("<==================>")
                                println(e.message)
                            }
                        }

                        override fun onSurfaceTextureSizeChanged(
                                surface: SurfaceTexture,
                                width: Int,
                                height: Int
                        ) {}
                        override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                            // Clean up resources here
                            return true
                        }

                        override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                    }
            println("==============> Done done")
        } catch (e: Exception) {
            println("<==================>")
            println("----> " + e.message)
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
