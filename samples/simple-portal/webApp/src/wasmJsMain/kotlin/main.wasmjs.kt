import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.eygraber.portal.samples.simpleportal.SimplePortal

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  CanvasBasedWindow("Simple Portal") {
    SimplePortal()
  }
}
