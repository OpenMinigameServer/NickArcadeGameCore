package io.github.openminigameserver.gamecore.core.arena.checks

import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition

data class ArenaValidityCheckResult(
    val valid: Boolean,
    val missingProperties: List<GamePropertyDefinition<*>>,
    val missingPropertiesRawName: List<String>
)
