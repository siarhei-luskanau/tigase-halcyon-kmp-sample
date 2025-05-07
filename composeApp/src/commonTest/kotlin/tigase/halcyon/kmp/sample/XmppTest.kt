package tigase.halcyon.kmp.sample

import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.test.runTest
import tigase.halcyon.core.AbstractHalcyon
import tigase.halcyon.core.HalcyonStateChangeEvent
import tigase.halcyon.core.ReflectionModuleManager
import tigase.halcyon.core.builder.createHalcyon
import tigase.halcyon.core.connector.ReceivedXMLElementEvent
import tigase.halcyon.core.connector.SentXMLElementEvent
import tigase.halcyon.core.xmpp.modules.PingModule
import tigase.halcyon.core.xmpp.toBareJID

class XmppTest {

    @OptIn(ReflectionModuleManager::class)
    @Test
    fun pingTest() {
        runTest {
            val halcyon = createHalcyon {
                platformInit(this)
                auth {
                    userJID = USER_JID.toBareJID()
                    password { USER_PASSWORD }
                }
            }

            var state: AbstractHalcyon.State? = null
            halcyon.eventBus.register(HalcyonStateChangeEvent) { event ->
                println("Halcyon:Event: StateChange: ${event.oldState}->${event.newState}")
                state = event.newState
            }
            halcyon.eventBus.register(SentXMLElementEvent) { event ->
                println("Halcyon:Event: XML>>> ${event.element.getAsString()}")
            }
            halcyon.eventBus.register(ReceivedXMLElementEvent) { event ->
                println("Halcyon:Event: XML<<< ${event.element.getAsString()}")
            }
            halcyon.connect()
            while (state != AbstractHalcyon.State.Connected) {
                delay(100.milliseconds)
            }

            val ping = suspendCancellableCoroutine { continuation ->
                halcyon.getModule<PingModule>()
                    .ping()
                    .response { response ->
                        response.onSuccess { successResult ->
                            continuation.resumeWith(Result.success(successResult.time.isPositive()))
                        }
                        response.onFailure { error ->
                            continuation.resumeWith(Result.failure(error))
                        }
                    }.send()
            }
            assertTrue(
                actual = ping,
                message = "ping should be true"
            )
        }
    }
}
