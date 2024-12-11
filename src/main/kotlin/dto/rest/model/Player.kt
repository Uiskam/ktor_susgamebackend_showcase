// WARNING: THIS FILE WAS CLONED AUTOMATICALLY FROM 'SusGameDTO' GITHUB REPOSITORY
// IT SHOULD NOT BE EDITED IN ANY WAY
// IN ORDER TO CHANGE THIS DTO, COMMIT TO 'SusGameDTO' GITHUB REPOSITORY
// IN ORDER TO UPDATE THIS FILE TO NEWEST VERSION, RUN 'scripts/update-DTO.sh'

package edu.agh.susgame.dto.rest.model

import edu.agh.susgame.dto.common.ColorDTO
import kotlinx.serialization.Serializable


@Serializable
data class PlayerId(val value: Int)

@Serializable
data class PlayerNickname(val value: String)

@Serializable
data class PlayerREST(
    val nickname: PlayerNickname,
    val id: PlayerId,
    val color: ColorDTO?,
    val readiness: Boolean,
)
