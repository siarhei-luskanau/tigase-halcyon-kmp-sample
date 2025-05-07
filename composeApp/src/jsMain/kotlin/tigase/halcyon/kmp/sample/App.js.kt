package tigase.halcyon.kmp.sample

import tigase.halcyon.core.builder.ConfigurationBuilder
import tigase.halcyon.core.builder.webSocketConnector

actual fun platformInit(configurationBuilder: ConfigurationBuilder) {
    configurationBuilder.apply {
        webSocketConnector {
            webSocketUrl = "ws://$XMPP_SERVER_ADDRESS:$XMPP_SERVER_PORT/ws"
            allowUnsecureConnection = true
        }
    }
}
