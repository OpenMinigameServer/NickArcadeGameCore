package io.github.openminigameserver.gamecore.core.game.properties

interface GameDefinitionPropertyContainer {
    val properties: MutableMap<GamePropertyType, MutableList<GamePropertyDefinition<*>>>
}

inline fun <reified T> GameDefinitionPropertyContainer.prop(
    name: String,
    friendlyName: String,
    type: GamePropertyType = GamePropertyType.DEFAULT
): GamePropertyDefinition<T> {
    return GamePropertyDefinition(name, friendlyName, type, T::class.java).also {
        properties.getOrPut(type) { mutableListOf() }.add(it)
    }
}

inline fun <reified T> GameDefinitionPropertyContainer.listProp(
    name: String,
    friendlyName: String,
    type: GamePropertyType = GamePropertyType.DEFAULT
): ListGamePropertyDefinition<T> {
    return ListGamePropertyDefinition<T>(name, friendlyName, type, T::class.java).also {
        properties.getOrPut(type) { mutableListOf() }.add(it)
    }
}