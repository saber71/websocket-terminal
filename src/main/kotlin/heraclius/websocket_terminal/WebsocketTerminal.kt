package heraclius.websocket_terminal

import heraclius.tools.TerminalAdapter
import io.ktor.websocket.Frame
import io.ktor.websocket.WebSocketSession
import io.ktor.websocket.readText
import kotlinx.coroutines.channels.ClosedReceiveChannelException

class WebsocketTerminal(val wsSession: WebSocketSession) : TerminalAdapter {
    companion object {
        private val instCache = mutableMapOf<WebSocketSession, WebsocketTerminal>()

        fun create(wsSession: WebSocketSession): WebsocketTerminal {
            return instCache.getOrPut(wsSession) { WebsocketTerminal(wsSession) }
        }
    }

    override suspend fun write(str: String) {
        wsSession.send(Frame.Text(str))
    }

    override suspend fun input(): String {
        try {
            val frame = wsSession.incoming.receive()
            if (frame is Frame.Text) {
                return frame.readText()
            }
            throw IllegalStateException("Expected text frame")
        } catch (e: ClosedReceiveChannelException) {
            instCache.remove(wsSession)
            throw e
        }
    }
}
