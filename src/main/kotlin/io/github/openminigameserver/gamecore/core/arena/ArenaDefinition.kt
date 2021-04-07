package io.github.openminigameserver.gamecore.core.arena

import com.fasterxml.jackson.annotation.JsonProperty
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import java.util.*

data class ArenaDefinition(
    val gameMode: GameModeDefinition,
    val name: String,
    val worldFileName: String,
    val spawnLocation: ArenaLocation,
    @JsonProperty("_id") val id: UUID = UUID.randomUUID(),
) {
    override fun toString(): String {
        return name
    }
}
