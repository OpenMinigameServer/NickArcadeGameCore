package io.github.openminigameserver.gamecore.core.game.properties

import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import kotlin.reflect.KProperty

class RequiredGamePropertyDefinition<T>(name: String, type: GamePropertyType, javaType: Class<T>) :
    GamePropertyDefinition<T>(
        name, type,
        javaType
    ) {
}

inline operator fun <reified V> RequiredGamePropertyDefinition<V>.getValue(
    definition: ArenaDefinition,
    property: KProperty<*>
): V {
    return definition[this]!!
}

inline operator fun <reified V> RequiredGamePropertyDefinition<V>.setValue(
    definition: ArenaDefinition,
    property: KProperty<*>,
    value: V
) {
    definition[this] = value
}