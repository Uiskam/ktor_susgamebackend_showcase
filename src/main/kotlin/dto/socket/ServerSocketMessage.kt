// WARNING: THIS FILE WAS CLONED AUTOMATICALLY FROM 'SusGameDTO' GITHUB REPOSITORY
// IT SHOULD NOT BE EDITED IN ANY WAY
// IN ORDER TO CHANGE THIS DTO, COMMIT TO 'SusGameDTO' GITHUB REPOSITORY
// IN ORDER TO UPDATE THIS FILE TO NEWEST VERSION, RUN 'scripts/update-DTO.sh'

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
    data class GameState(
        val routers: List<RouterDTO>,
        val servers: List<ServerDTO>,
        val hosts: List<HostDTO>,
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
}
