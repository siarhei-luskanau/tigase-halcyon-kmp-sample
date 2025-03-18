import androidx.compose.ui.window.ComposeUIViewController
import tigase.halcyon.kmp.sample.App
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController = ComposeUIViewController { App() }
