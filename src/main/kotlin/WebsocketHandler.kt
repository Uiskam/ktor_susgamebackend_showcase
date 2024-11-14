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

                        is ServerSocketMessage.GameState -> {
                            val edges = receivedMessage.edges
                            val players = receivedMessage.players
                            println(receivedMessage)
                            println()
                        }

                        is ServerSocketMessage.ServerError -> {
                            println("received ERROR : ${receivedMessage.errorMessage}")
                        }

                        is ServerSocketMessage.QuizQuestionDTO -> {
                            println("received QUESTION: ${receivedMessage.question}")
                            println("received ANSWERS: ${receivedMessage.answers}")
                            println("received CORRECT ANSWER: ${receivedMessage.correctAnswer}")
                        }

                        is ServerSocketMessage.PlayerChangeReadinessResponse -> println("received ${receivedMessage.playerId} is ${if (receivedMessage.state) "ready" else "unready"}")
                        is ServerSocketMessage.PlayerJoiningResponse ->  println("received: ${receivedMessage.playerId}: ${receivedMessage.playerName} joined")
                        is ServerSocketMessage.PlayerLeavingResponse -> println("received: ${receivedMessage.playerId} left")
                        is ServerSocketMessage.IdConfig -> println("received: your index is ${receivedMessage.id}")
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
suspend fun DefaultClientWebSocketSession.inputMessages() {
    val message: ClientSocketMessage = ClientSocketMessage.PlayerJoiningRequest("test-name")
    try {
        send(Cbor.encodeToByteArray(message))
    } catch (e: Exception) {
        println("Error while sending: " + e.localizedMessage)
        return
    }
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
            "ready" -> {
                val message: ClientSocketMessage = ClientSocketMessage.PlayerChangeReadinessRequest(0, true)
                try {
                    send(Cbor.encodeToByteArray(message))
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            "unready" -> {
                val message: ClientSocketMessage = ClientSocketMessage.PlayerChangeReadinessRequest(0, false)
                try {
                    send(Cbor.encodeToByteArray(message))
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            "setPath" -> {
                val message: ClientSocketMessage = ClientSocketMessage.HostDTO(1, listOf(2, 3), 1)
                try {
                    send(Cbor.encodeToByteArray(message))
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            "upgrade" -> {
                val message: ClientSocketMessage = ClientSocketMessage.UpgradeDTO(1)
                try {
                    send(Cbor.encodeToByteArray(message))
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
            "ans" -> {
                println("Enter questionId,answer:")
                val input = readlnOrNull() ?: ""
                // Check if the input matches the format "Int,Int"
                if (Regex("""^\d+,\d+$""").matches(input)) {
                    // Split the input and convert each part to Int
                    val parts = input.split(",")
                    val questionId = parts[0].toInt()
                    val answer = parts[1].toInt()

                    // Print the extracted values
                    println("questionId: $questionId")
                    println("answer: $answer")
                    val message: ClientSocketMessage = ClientSocketMessage.QuizAnswerDTO(questionId, answer)
                    try {
                        send(Cbor.encodeToByteArray(message))
                    } catch (e: Exception) {
                        println("Error while sending: " + e.localizedMessage)
                        return
                    }
                } else {
                    println("Invalid format. Please enter in 'Int,Int' format.")
                }
            }
            "exit" -> {
                val message: ClientSocketMessage = ClientSocketMessage.PlayerLeavingRequest(playerId = 0)
                try {
                    send(Cbor.encodeToByteArray(message))
                    return
                } catch (e: Exception) {
                    println("Error while sending: " + e.localizedMessage)
                    return
                }
            }
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