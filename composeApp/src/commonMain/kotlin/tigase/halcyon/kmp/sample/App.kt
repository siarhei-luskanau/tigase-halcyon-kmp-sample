package tigase.halcyon.kmp.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import tigase.halcyon.core.HalcyonStateChangeEvent
import tigase.halcyon.core.builder.ConfigurationBuilder
import tigase.halcyon.core.builder.createHalcyon
import tigase.halcyon.core.connector.ReceivedXMLElementEvent
import tigase.halcyon.core.connector.SentXMLElementEvent
import tigase.halcyon.core.xmpp.modules.PingModule
import tigase.halcyon.core.xmpp.toBareJID
import tigase.halcyon.core.xmpp.toJID
import tigase.halcyon.kmp.sample.theme.AppTheme
import kotlin.time.Duration.Companion.seconds
import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
internal fun App() = AppTheme {
    val logs = remember { mutableStateListOf<String>() }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.safeDrawing)
            .padding(16.dp)
    ) {
        Text(
            text = "$XMPP_SERVER_ADDRESS:$XMPP_SERVER_PORT\n$USER_JID",
            style = MaterialTheme.typography.headlineSmall
        )
        LazyColumn {
            items(logs) { log ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = log,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.Default) {
            val user = USER_JID.toBareJID()
            val halcyon = createHalcyon {
                platformInit(this)
                auth {
                    userJID = user
                    password { USER_PASSWORD }
                }
            }

            halcyon.eventBus.register(HalcyonStateChangeEvent) { event ->
                logs.add("StateChange: ${event.oldState}->${event.newState}")
            }
            halcyon.eventBus.register(SentXMLElementEvent) { event ->
                logs.add("XML>>> ${event.element.getAsString()}")
            }
            halcyon.eventBus.register(ReceivedXMLElementEvent) { event ->
                logs.add("XML<<< ${event.element.getAsString()}")
            }

            halcyon.connect()

            halcyon.getModule(PingModule)
                .ping(user.domain.toJID())
                .send()

            delay(60.seconds)

            halcyon.disconnect()
        }
    }
}

expect fun platformInit(configurationBuilder: ConfigurationBuilder)
