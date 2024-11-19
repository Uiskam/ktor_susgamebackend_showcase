// WARNING: THIS FILE WAS CLONED AUTOMATICALLY FROM 'SusGameDTO' GITHUB REPOSITORY
// IT SHOULD NOT BE EDITED IN ANY WAY
// IN ORDER TO CHANGE THIS DTO, COMMIT TO 'SusGameDTO' GITHUB REPOSITORY
// IN ORDER TO UPDATE THIS FILE TO NEWEST VERSION, RUN 'scripts/update-DTO.sh'\n\n

package edu.agh.susgame.dto.socket

import edu.agh.susgame.dto.socket.common.GameStatus
import edu.agh.susgame.dto.socket.server.*
import kotlinx.serialization.Serializable

/**
 * Message sent by server via WebSocket
 */
@Serializable
sealed class ServerSocketMessage {
    @Serializable
    data class IdConfig(
        val id: Int
    ) : ServerSocketMessage()
    /**
     * Information about the whole game state that is coming periodically from the server
     */
    @Serializable
    data class GameState(
        val routers: List<RouterDTO>,
        val hosts: List<HostDTO>,
        val server: ServerDTO,
        val edges: List<EdgeDTO>,
        val players: List<PlayerDTO>,
        val gameStatus: GameStatus,
    ) : ServerSocketMessage()

    /**
     * Used to send error messages (for example in the case of invalid input)
     */
    @Serializable
    data class ServerError(
        val errorMessage: String,
    ) : ServerSocketMessage()

    /**
     * Used for chat feature, which is planned to be deleted in the future
     */
    @Serializable
    data class ChatMessage(
        val authorNickname: String,
        val message: String,
    ) : ServerSocketMessage()

    @Serializable
    data class QuizQuestionDTO(
        val questionId: Int,
        val question: String,
        val answers: List<String>,
        val correctAnswer: Int,
    ) : ServerSocketMessage()

    @Serializable
    data class PlayerJoiningResponse(
        val playerId: Int,
        val playerName: String
    ) : ServerSocketMessage()

    /**
     * Used for handling player changing state in lobby
     */
    @Serializable
    data class PlayerChangeReadinessResponse(
        val playerId: Int,
        val state: Boolean
    ) : ServerSocketMessage()

    /**
     * Used for handling player leaving lobby
     */
    @Serializable
    data class PlayerLeavingResponse(
        val playerId: Int
    ) : ServerSocketMessage()
}
