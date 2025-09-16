import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.eygraber.portal.samples.simpleportal.shared.SimplePortal

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  ComposeViewport {
    SimplePortal()
  }
}
