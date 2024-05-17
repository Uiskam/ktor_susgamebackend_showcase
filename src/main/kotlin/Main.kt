package org.example

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.websocket.*
import kotlinx.coroutines.*

suspend fun main() {
    val RESTClient  = HttpClient(CIO)
    val address = "http://0.0.0.0:8080"
    //val response: HttpResponse = RESTClient .get("http://0.0.0.0/8080")
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
                val response: HttpResponse = RESTClient .get("$address/games")
                println(response.bodyAsText())
            }

            "2" -> {
                println("Enter game id:")
                val id = readlnOrNull() ?: ""
                val response: HttpResponse = RESTClient .get("$address/games/$id")
                println(response.bodyAsText())
            }

            "3" -> {
                println("Enter game name:")
                val name = readlnOrNull() ?: ""
                val response: HttpResponse = RESTClient .post("$address/games/$name")
                println(response.bodyAsText())
            }

            "4" -> {
                println("Enter game id:")
                val id = readlnOrNull() ?: ""
                val response: HttpResponse = RESTClient .delete("$address/games/$id")
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
                    webSocketClient.webSocket(method = HttpMethod.Get, host = "0.0.0.0", port = 8080, path = "/games/join?gameName=$id&playerName=$playerName") {
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
