import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.eygraber.portal.samples.kotlin.inject.appComponent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  val mainPortal = appComponent.mainPortal

  CanvasBasedWindow("KotlinInject") {
    mainPortal.Render()
  }
}
