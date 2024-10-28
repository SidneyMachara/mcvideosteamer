import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import com.example.mcvideostreamer.R
import io.flutter.plugin.platform.PlatformView

class CameraPreviewView(context: Context) : PlatformView {
    // private val surfaceView: SurfaceView
    private val textureView: TextureView

    init {
        // Inflate the native XML layout
        val view = LayoutInflater.from(context).inflate(R.layout.camera_preview, null)
        // surfaceView = view.findViewById<SurfaceView>(R.id.camera_preview)
        textureView = view.findViewById<TextureView>(R.id.camera_preview_texture)
    }

    override fun getView(): View {
        // return surfaceView
        return textureView
    }

    override fun dispose() {
        // Properly detach the view to avoid reuse issues
        (textureView.parent as? ViewGroup)?.removeView(textureView)
    }
}
