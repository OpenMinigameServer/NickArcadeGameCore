package io.github.openminigameserver.gamecore.core.arena

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import io.github.openminigameserver.gamecore.core.arena.checks.ArenaValidityCheckResult
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.RequiredGamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.getValue
import io.github.openminigameserver.gamecore.core.game.properties.setValue
import io.github.openminigameserver.hypixelapi.HypixelApi
import java.util.*

data class ArenaDefinition(
    val gameMode: GameModeDefinition,
    val name: String,
    val worldFileName: String,
    @PublishedApi internal val properties: MutableMap<String, JsonNode> = mutableMapOf(),
    @JsonProperty("_id") val id: UUID = UUID.randomUUID(),
) {
    fun checkValidity(): ArenaValidityCheckResult {
        val missingProperties = gameMode.properties.values.filterIsInstance<RequiredGamePropertyDefinition<*>>().filterNot {
            properties.containsKey(it.name)
        }

        return ArenaValidityCheckResult(missingProperties.isEmpty(), missingProperties)
    }

    val isValid
        get() = checkValidity().valid

    var spawnLocation: ArenaLocation by gameMode.spawnLocation

    override fun toString(): String {
        return name
    }

    @JsonIgnore
    inline operator fun <reified T> get(prop: GamePropertyDefinition<T>): T? {
        return properties[prop.name]?.let { HypixelApi.objectMapper.treeToValue(it, T::class.java) }
    }

    @JsonIgnore
    inline operator fun <reified T> set(prop: GamePropertyDefinition<T>, value: T?) {
        if (value == null) {
            properties.remove(prop.name)
            return
        }
        properties[prop.name] = HypixelApi.objectMapper.valueToTree(value)
    }
}
