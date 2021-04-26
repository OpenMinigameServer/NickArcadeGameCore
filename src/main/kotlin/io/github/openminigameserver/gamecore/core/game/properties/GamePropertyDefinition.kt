package io.github.openminigameserver.gamecore.core.game.properties

data class GamePropertyDefinition<T>(val name: String, val type: GamePropertyType, val javaType: Class<T>)

enum class GamePropertyType {
    DEFAULT,
    MODE,
    ARENA
}
