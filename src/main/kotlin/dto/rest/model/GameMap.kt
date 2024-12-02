// WARNING: THIS FILE WAS CLONED AUTOMATICALLY FROM 'SusGameDTO' GITHUB REPOSITORY
// IT SHOULD NOT BE EDITED IN ANY WAY
// IN ORDER TO CHANGE THIS DTO, COMMIT TO 'SusGameDTO' GITHUB REPOSITORY
// IN ORDER TO UPDATE THIS FILE TO NEWEST VERSION, RUN 'scripts/update-DTO.sh'

package edu.agh.susgame.dto.rest.model

import kotlinx.serialization.Serializable


@Serializable
data class Coordinates(val x: Int, val y: Int)


@Serializable
sealed class GameMapNodeDTO {
    abstract val id: Int
    abstract val coordinates: Coordinates

    data class Server(override val id: Int, override val coordinates: Coordinates) : GameMapNodeDTO()

    data class Host(override val id: Int, override val coordinates: Coordinates) : GameMapNodeDTO()

    data class Router(
        override val id: Int,
        override val coordinates: Coordinates,
        val bufferSize: Int
    ) : GameMapNodeDTO()
}


@Serializable
data class GameMapEdgeDTO(val from: Int, val to: Int, val weight: Int)
