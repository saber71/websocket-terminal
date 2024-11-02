package heraclius.websocket_terminal

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.http.content.staticResources
import io.ktor.server.routing.routing
import io.ktor.server.websocket.WebSockets
import io.ktor.server.websocket.pingPeriod
import io.ktor.server.websocket.timeout
import io.ktor.server.websocket.webSocket
import io.ktor.websocket.WebSocketSession
import kotlin.time.Duration.Companion.seconds

private var onConnectFn: (suspend (WebSocketSession) -> Unit)? = null

fun Application.onWsTerminalConnect(fn: suspend (WebSocketSession) -> Unit) {
    onConnectFn = fn
}

fun Application.wsTerminalModule() {
    install(WebSockets) {
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        staticResources("/", "static")
        webSocket("/terminal") {
            onConnectFn?.invoke(this)
        }
    }
}
