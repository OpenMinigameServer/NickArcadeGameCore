package io.github.openminigameserver.gamecore.core.arena

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator
import io.github.openminigameserver.gamecore.core.arena.checks.ArenaValidityCheckResult
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.*
import io.github.openminigameserver.hypixelapi.HypixelApi
import java.util.*

data class ArenaDefinition(
    val gameMode: GameModeDefinition,
    val name: String,
    val worldFileName: String,
    @PublishedApi internal val properties: MutableMap<String, WrappedPropertyValue> = mutableMapOf(),
    @JsonProperty("_id") val id: UUID = UUID.randomUUID(),
) {
    fun checkValidity(): ArenaValidityCheckResult {
        val missingProperties =
            gameMode.properties.values.filterIsInstance<RequiredGamePropertyDefinition<*>>().filterNot {
                properties.containsKey(it.name)
            }

        return ArenaValidityCheckResult(missingProperties.isEmpty(), missingProperties)
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

    companion object {
        val mapper: ObjectMapper = HypixelApi.objectMapper.copy().activateDefaultTyping(
            BasicPolymorphicTypeValidator.builder().allowIfBaseType(Any::class.java).build(),
            ObjectMapper.DefaultTyping.EVERYTHING,
            JsonTypeInfo.As.PROPERTY
        )
    }

    @JsonIgnore
    inline operator fun <reified T> get(prop: GamePropertyDefinition<T>): T? {
        if (prop.type != GamePropertyType.DEFAULT && prop.type != GamePropertyType.ARENA) throw Exception("Property ${prop.friendlyName} is not a property that can be used in arenas.")
        return properties[prop.name]?.let { it.value as T }
    }

    @JsonIgnore
    inline operator fun <reified T> set(prop: GamePropertyDefinition<T>, value: T?) {
        if (value == null) {
            properties.remove(prop.name)
            return
        }
        properties[prop.name] = WrappedPropertyValue(value)
    }
}
