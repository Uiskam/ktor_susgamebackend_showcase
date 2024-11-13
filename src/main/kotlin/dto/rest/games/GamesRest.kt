// WARNING: THIS FILE WAS CLONED AUTOMATICALLY FROM 'SusGameDTO' GITHUB REPOSITORY
// IT SHOULD NOT BE EDITED IN ANY WAY
// IN ORDER TO CHANGE THIS DTO, COMMIT TO 'SusGameDTO' GITHUB REPOSITORY
// IN ORDER TO UPDATE THIS FILE TO NEWEST VERSION, RUN 'scripts/update-DTO.sh'

package edu.agh.susgame.dto.rest.games

import edu.agh.susgame.dto.rest.games.model.CreateGameApiResult
import edu.agh.susgame.dto.rest.games.model.GetAllGamesApiResult
import edu.agh.susgame.dto.rest.games.model.GetGameApiResult
import edu.agh.susgame.dto.rest.games.model.GetGameMapApiResult
import edu.agh.susgame.dto.rest.model.LobbyId
import java.util.concurrent.CompletableFuture

interface GamesRest {
    fun getAllGames(): CompletableFuture<GetAllGamesApiResult>

    fun getGame(gameId: LobbyId): CompletableFuture<GetGameApiResult>

    fun getGameMap(gameId: LobbyId): CompletableFuture<GetGameMapApiResult>

    fun createGame(
        gameName: String,
        maxNumberOfPlayers: Int,
        gamePin: String? = null,
    ): CompletableFuture<CreateGameApiResult>
}
