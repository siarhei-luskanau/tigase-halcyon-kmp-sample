import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import tigase.halcyon.kmp.sample.App

fun main() = application {
    Window(
        title = "Halcyon Multiplatform Sample App",
        state = rememberWindowState(width = 800.dp, height = 600.dp),
        onCloseRequest = ::exitApplication
    ) {
        window.minimumSize = Dimension(350, 600)
        App()
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
