package io.github.openminigameserver.gamecore.core.game.properties

interface GamePropertyContainer {
    val properties: MutableMap<GamePropertyType, MutableList<GamePropertyDefinition<*>>>
}

inline fun <reified T> GamePropertyContainer.prop(name: String, type: GamePropertyType = GamePropertyType.DEFAULT): GamePropertyDefinition<T> {
    return GamePropertyDefinition(name, type, T::class.java).also {
        properties.getOrPut(type) { mutableListOf() }.add(it)
    }
}