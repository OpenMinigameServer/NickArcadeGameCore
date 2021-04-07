package io.github.openminigameserver.gamecore.core.arena.manager.io.json

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import io.github.openminigameserver.gamecore.core.arena.manager.io.SerializedMiniGameMode
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition

object MiniGameModeSerializer : StdSerializer<GameModeDefinition>(GameModeDefinition::class.java) {
    override fun serialize(value: GameModeDefinition, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeObject(
            SerializedMiniGameMode(value.game.name, value.name)
        )
    }
}