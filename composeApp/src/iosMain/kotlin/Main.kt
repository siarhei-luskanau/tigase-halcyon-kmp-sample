import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController
import tigase.halcyon.kmp.sample.App

fun mainViewController(): UIViewController = ComposeUIViewController { App() }
