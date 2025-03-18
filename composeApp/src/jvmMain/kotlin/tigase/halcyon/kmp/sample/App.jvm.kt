package tigase.halcyon.kmp.sample

import tigase.halcyon.core.builder.ConfigurationBuilder
import tigase.halcyon.core.builder.socketConnector

actual fun platformInit(configurationBuilder: ConfigurationBuilder) {
    configurationBuilder.apply {
        socketConnector {
            hostname = XMPP_SERVER_ADDRESS
            port = XMPP_SERVER_PORT
        }
    }
}
