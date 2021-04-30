package io.github.openminigameserver.gamecore.core.game.properties

import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import kotlin.reflect.KProperty

open class GamePropertyDefinition<T>(
    val name: String,
    val friendlyName: String,
    val type: GamePropertyType,
    val javaType: Class<T>
)

inline operator fun <reified T> GamePropertyDefinition<T>.getValue(
    definition: ArenaDefinition,
    property: KProperty<*>
): T? {
    return definition[this]
}

inline operator fun <reified V> GamePropertyDefinition<V>.setValue(
    definition: ArenaDefinition,
    property: KProperty<*>,
    value: V?
) {
    definition[this] = value
}


