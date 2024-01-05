import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.eygraber.portal.samples.simpleportal.SimplePortal
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  onWasmReady {
    CanvasBasedWindow("Simple Portal") {
      SimplePortal()
    }
  }
}
