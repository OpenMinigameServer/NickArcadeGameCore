package io.github.openminigameserver.gamecore.core.game.properties

import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import kotlin.reflect.KProperty


class RequiredListGamePropertyDefinition<T>(name: String, friendlyName: String, type: GamePropertyType) :
    RequiredGamePropertyDefinition<MutableList<*>>(
        name, friendlyName, type,
        MutableList::class.java
    )

inline operator fun <reified V> RequiredListGamePropertyDefinition<V>.getValue(
    definition: ArenaDefinition,
    property: KProperty<*>
): MutableList<V> {
    return definition[this] as MutableList<V>
}

inline operator fun <reified V> RequiredListGamePropertyDefinition<V>.setValue(
    definition: ArenaDefinition,
    property: KProperty<*>,
    value: MutableList<V>
) {
    definition[this] = value
}