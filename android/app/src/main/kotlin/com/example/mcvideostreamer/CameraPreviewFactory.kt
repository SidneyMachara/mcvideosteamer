import android.content.Context
import io.flutter.plugin.common.StandardMessageCodec
import io.flutter.plugin.platform.PlatformView
import io.flutter.plugin.platform.PlatformViewFactory

class CameraPreviewViewFactory : PlatformViewFactory(StandardMessageCodec.INSTANCE) {
    private var cameraPreviewView: CameraPreviewView? = null

    override fun create(context: Context, id: Int, args: Any?): PlatformView {
        cameraPreviewView = CameraPreviewView(context)
        return cameraPreviewView!!
    }

    fun getCameraPreviewView(): CameraPreviewView? {
        return cameraPreviewView
    }
}
