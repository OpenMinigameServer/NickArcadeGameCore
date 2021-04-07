package io.github.openminigameserver.gamecore.core.arena.manager.io.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import io.github.openminigameserver.gamecore.core.arena.manager.io.SerializedMiniGameMode
import io.github.openminigameserver.gamecore.core.game.GameManager
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition

object MiniGameModeDeserializer : StdDeserializer<GameModeDefinition>(GameModeDefinition::class.java) {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): GameModeDefinition? {
        val serializedMiniGameMode = p.readValueAs(SerializedMiniGameMode::class.java)
        return GameManager.registeredGames[serializedMiniGameMode.gameName]?.gameModes?.get(serializedMiniGameMode.modeName)
    }
}