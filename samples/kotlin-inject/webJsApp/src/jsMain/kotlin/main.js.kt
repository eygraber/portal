import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import com.eygraber.portal.samples.kotlin.inject.appComponent

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
  val mainPortal = appComponent.mainPortal

  ComposeViewport {
    @Suppress("UnnecessaryFullyQualifiedName")
    mainPortal.Render()
  }
}
