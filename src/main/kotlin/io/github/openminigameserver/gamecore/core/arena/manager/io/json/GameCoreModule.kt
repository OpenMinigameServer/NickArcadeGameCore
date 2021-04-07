package io.github.openminigameserver.gamecore.core.arena.manager.io.json

import com.fasterxml.jackson.databind.module.SimpleModule
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition

object GameCoreModule : SimpleModule("GameCore") {
    init {
        addSerializer(GameModeDefinition::class.java, MiniGameModeSerializer)
        addDeserializer(GameModeDefinition::class.java, MiniGameModeDeserializer)
    }
}