package io.github.openminigameserver.gamecore.core.game.properties

interface GameDefinitionPropertyContainer {
    val properties: MutableMap<GamePropertyType, MutableList<GamePropertyDefinition<*>>>
}

inline fun <reified T> GameDefinitionPropertyContainer.prop(name: String, type: GamePropertyType = GamePropertyType.DEFAULT): GamePropertyDefinition<T> {
    return GamePropertyDefinition(name, type, T::class.java).also {
        properties.getOrPut(type) { mutableListOf() }.add(it)
    }
}
inline fun <reified T> GameDefinitionPropertyContainer.requiredProp(name: String, type: GamePropertyType = GamePropertyType.DEFAULT): RequiredGamePropertyDefinition<T> {
    return RequiredGamePropertyDefinition(name, type, T::class.java).also {
        properties.getOrPut(type) { mutableListOf() }.add(it)
    }
}