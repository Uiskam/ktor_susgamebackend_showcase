package org.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class GameCreationRequest(val gameName: String, val maxNumberOfPlayers: Int = 4, val gamePin: String? = null)


const val HOST = "192.168.0.105"


suspend fun main() {
    val RESTClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                isLenient = true
                ignoreUnknownKeys = true
                allowSpecialFloatingPointValues = true
                useArrayPolymorphism = false
            })
        }
    }
    val port = 8080

    val address = "http://$HOST:$port"

    while (true) {
        println("1. Get all available games")
        println("2. Get specific game")
        println("3. Create a new game")
        println("4. Delete a game")
        println("5. Join a game")
        println("8. Exit")
        println("Choose an action:")
        //clear console
        val action = readlnOrNull() ?: ""
        when (action) {
            "1" -> {
                val response: HttpResponse = RESTClient.get("$address/games")
                println(response.bodyAsText())
            }

            "2" -> {
                println("Enter game id:")
                val id = readlnOrNull() ?: ""
                val response: HttpResponse = RESTClient.get("$address/games/$id")
                println(response.bodyAsText())
            }

            "3" -> {
                println("Enter game name:")
                val name = readlnOrNull() ?: ""
                println("Enter max number of players:")
                val maxPlayers = readlnOrNull() ?: ""
                println("Enter game pin:")
                val pin = readlnOrNull() ?: ""

                val response: HttpResponse = RESTClient.post("$address/games") {
                    contentType(ContentType.Application.Json)
                    setBody(GameCreationRequest(name, maxPlayers.toInt(), pin))
                }
                println("RES body: ${response.bodyAsText()}")
            }

            "4" -> {
                println("Enter game id:")
                val id = readlnOrNull() ?: ""
                val response: HttpResponse = RESTClient.delete("$address/games/$id")
                println(response.bodyAsText())
            }

            "5" -> {
                println("Enter game id:")
                val id = readlnOrNull() ?: ""
                println("Enter player name:")
                val playerName = readlnOrNull() ?: ""
                val webSocketClient = HttpClient {
                    install(WebSockets)
                }
                println("Type exit to end the session")
                runBlocking {
                    webSocketClient.webSocket(
                        method = HttpMethod.Get,
                        host = HOST,
                        port = port,
                        path = "/games/join?gameId=$id&playerName=$playerName"
                    ) {
                        val messageOutputRoutine = launch { outputMessages() }
                        val userInputRoutine = launch { inputMessages() }

                        userInputRoutine.join() // Wait for completion; either "exit" or error
                        messageOutputRoutine.cancelAndJoin()
                    }
                }
                webSocketClient.close()
                println("Connection closed. Goodbye!")
            }

            "8" -> {
                return
            }

            else -> {
                println("Invalid action")
            }
        }

        //println(response.status)
        //client.close()
    }
}
