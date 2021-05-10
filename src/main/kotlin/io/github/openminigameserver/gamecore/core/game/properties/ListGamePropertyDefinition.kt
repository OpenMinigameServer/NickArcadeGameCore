package io.github.openminigameserver.gamecore.core.game.properties

import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import kotlin.reflect.KProperty

class ListGamePropertyDefinition<T>(name: String, friendlyName: String, type: GamePropertyType, listClass: Class<T>) :
    GamePropertyDefinition<MutableList<*>>(
        name, friendlyName, type,
        MutableList::class.java
    ) {
        val listType = listClass
    }

inline operator fun <reified V> ListGamePropertyDefinition<V>.getValue(
    definition: ArenaDefinition,
    property: KProperty<*>
): MutableList<V>? {
    return (definition[this] as? MutableList<V>)
}

inline operator fun <reified V> ListGamePropertyDefinition<V>.setValue(
    definition: ArenaDefinition,
    property: KProperty<*>,
    value: MutableList<V>?
) {
    definition[this] = value
}