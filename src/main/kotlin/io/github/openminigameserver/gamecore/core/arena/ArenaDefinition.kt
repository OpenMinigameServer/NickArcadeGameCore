package io.github.openminigameserver.gamecore.core.arena

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import io.github.openminigameserver.gamecore.core.arena.checks.ArenaValidityCheckResult
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.*
import io.github.openminigameserver.gamecore.core.team.GameTeam
import java.util.*

data class  ArenaDefinition(
    val gameMode: GameModeDefinition,
    val name: String,
    val worldFileName: String,
    @PublishedApi internal val properties: MutableMap<String, WrappedPropertyValue> = mutableMapOf(),
    @JsonProperty("_id") val id: UUID = UUID.randomUUID(),
) {
    fun checkValidity(): ArenaValidityCheckResult {
        val missingPropRaw = mutableListOf<String>()
        val missingProperties =
            gameMode.properties.values.flatten().filterIsInstance<RequiredGamePropertyDefinition<*>>().filterNot { prop ->
                val propertyNames = getPropertyNames(gameMode, prop)
                propertyNames.all { name -> properties.containsKey(name) }.also {
                    if (!it) missingPropRaw.addAll(propertyNames)
                }
            }

        return ArenaValidityCheckResult(missingProperties.isEmpty(), missingProperties, missingPropRaw)
    }

    private fun getPropertyNames(gameMode: GameModeDefinition, prop: GamePropertyDefinition<*>): List<String> {
        if (prop.type == GamePropertyType.TEAM) {
            return gameMode.modeTeams.map { "team_${prop.name}_${it().name}" }
        }
        return listOf(prop.name)
    }

    @get:JsonIgnore
    val isValid
        get() = checkValidity().valid

    @get:JsonIgnore
    @set:JsonIgnore
    var spawnLocation: ArenaLocation by gameMode.spawnLocation

    override fun toString(): String {
        return name
    }

    @JsonIgnore
    inline operator fun <reified T> set(prop: GamePropertyDefinition<T>, value: T?) {
        set(prop, value, null)
    }

    @JsonIgnore
    inline operator fun <reified T> get(prop: GamePropertyDefinition<T>, team: GameTeam? = null): T? {
        var name = prop.name
        if (team != null) {
            name = "team_${prop.name}_${team.name}"
        }
        return properties[name]?.let { it.value as T }
    }

    @JsonIgnore
    inline fun <reified T> set(prop: GamePropertyDefinition<T>, value: T?, team: GameTeam? = null) {
        var name = prop.name
        if (team != null) {
            name = "team_${prop.name}_${team.name}"
        }
        if (value == null) {
            properties.remove(name)
            return
        }
        properties[name] = WrappedPropertyValue(value, T::class.java.name)
    }
}
