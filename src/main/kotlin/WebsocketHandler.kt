/*
Wtedy wiadomość socketowa wyglądałaby mniej więcej w taki sposób:
@Serializable
sealed class ClientSocketMessage {
    @Serializable
    data class GameState(
        val gameGraph: NetGraph
    ) : ClientSocketMessage()

    @Serializable
    data class SimpleMessage(
        val authorNickname: String,
        val message: String,
    ) : ClientSocketMessage()
}


Wysyłanie takiej wiadomości odbywałoby się mniej więcej tak:
val message: ClientSocketMessage = ClientSocketMessage.SimpleMessage(
	authorNickname = playerName,
	message = receivedText,
)
val encodedMessage = Cbor.encodeToByteArray(message)
it.key.session.send(encodedMessage)

 */
package org.example

import edu.agh.susgame.dto.socket.ClientSocketMessage
import edu.agh.susgame.dto.socket.ServerSocketMessage
import edu.agh.susgame.dto.socket.common.GameStatus
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.cbor.Cbor
import kotlinx.serialization.decodeFromByteArray
import kotlinx.serialization.encodeToByteArray
import kotlinx.serialization.modules.SerializersModule

@OptIn(ExperimentalSerializationApi::class)
private val cbor = Cbor {
    serializersModule = SerializersModule {
        contextual(ClientSocketMessage::class, ClientSocketMessage.serializer())
    }
}

@OptIn(ExperimentalSerializationApi::class)
suspend fun DefaultClientWebSocketSession.outputMessages() {
    try {
        for (message in incoming) {
            when (message) {
                is Frame.Text -> {
                    println(message.readText())
                }

                is Frame.Binary -> {
                    val receivedEncodedMessage = message.data
                    when (val receivedMessage =
                        cbor.decodeFromByteArray<ServerSocketMessage>(receivedEncodedMessage)) {

                        is ServerSocketMessage.ChatMessage -> {
                            println("received binary:")
                            val sender = receivedMessage.authorNickname
                            println("received binary: $sender")
                            val messageText = receivedMessage.message
                            println("received binary: $messageText")
                        }

                        else -> {
                            println("received UNKNOWN binary: $receivedMessage")
                        }
                    }
                }

                is Frame.Close -> TODO()
                is Frame.Ping -> TODO()
                is Frame.Pong -> TODO()
            }
        }
    } catch (e: Exception) {
        println("Error while receiving: " + e.localizedMessage)
    }
}



@OptIn(ExperimentalSerializationApi::class)
suspend fun DefaultClientWebSocketSession.inputMessagesOLD() {
    while (true) {
        val command = readLine() ?: ""
        if (command.equals("exit", true)) return
//        val message: ClientSocketMessage = ClientSocketMessage.SimpleMessage(
//            authorNickname = "playerName",
//            message = "receivedText",
//        )
        val message: ClientSocketMessage = ClientSocketMessage.ChatMessage(
            command
        )
        try {
            if (!command.equals("b", true)) {
                send(command)
            } else {
                send(Cbor.encodeToByteArray(message))
            }


        } catch (e: Exception) {
            println("Error while sending: " + e.localizedMessage)
            return
        }
    }
}

@OptIn(ExperimentalSerializationApi::class)
suspend fun DefaultClientWebSocketSession.inputMessages() {
    while (true) {
        val command = readlnOrNull() ?: ""
        when (command) {
            "start" -> {
                val message: ClientSocketMessage = ClientSocketMessage.GameState(GameStatus.RUNNING)
                try {
                    send(Cbor.encodeToByteArray(message))
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            "exit" -> return
            else -> {
                val message: ClientSocketMessage = ClientSocketMessage.ChatMessage(command)
                try {
                    send(Cbor.encodeToByteArray(message))
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
        }

    }
}