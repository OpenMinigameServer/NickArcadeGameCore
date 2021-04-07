package io.github.openminigameserver.gamecore.core.game.hosting

enum class GameHostingMode(val description: String) {
    PUBLIC("Public Game"),
    PRIVATE_PARTY("Private Game (hosted by Party)"),
    PRIVATE_ADMIN("Private Game (hosted by Admin)")
}