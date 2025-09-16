import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.eygraber.portal.samples.simpleportal.shared.SimplePortal
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  onWasmReady {
    ComposeViewport {
      SimplePortal()
    }
  }
}
