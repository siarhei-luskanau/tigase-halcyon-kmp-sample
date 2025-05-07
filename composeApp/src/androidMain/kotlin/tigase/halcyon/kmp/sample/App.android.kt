package tigase.halcyon.kmp.sample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import tigase.halcyon.core.builder.ConfigurationBuilder
import tigase.halcyon.core.builder.socketConnector

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent { App() }
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}

actual fun platformInit(configurationBuilder: ConfigurationBuilder) {
    configurationBuilder.apply {
        socketConnector {
            hostname = XMPP_SERVER_ADDRESS
            port = XMPP_SERVER_PORT
        }
    }
}
