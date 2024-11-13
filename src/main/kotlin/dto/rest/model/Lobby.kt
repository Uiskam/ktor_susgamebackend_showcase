// WARNING: THIS FILE WAS CLONED AUTOMATICALLY FROM 'SusGameDTO' GITHUB REPOSITORY
// IT SHOULD NOT BE EDITED IN ANY WAY
// IN ORDER TO CHANGE THIS DTO, COMMIT TO 'SusGameDTO' GITHUB REPOSITORY
// IN ORDER TO UPDATE THIS FILE TO NEWEST VERSION, RUN 'scripts/update-DTO.sh'

package edu.agh.susgame.dto.rest.model

import kotlinx.serialization.Serializable


@Serializable
data class LobbyId(val value: Int)

@Serializable
data class Lobby(
    val id: LobbyId,
    val name: String,
    val maxNumOfPlayers: Int,
    val gameTime: Int,
    val playersWaiting: List<PlayerREST>,
)
