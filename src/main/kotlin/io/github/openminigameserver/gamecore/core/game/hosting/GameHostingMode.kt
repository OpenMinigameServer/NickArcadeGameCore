package io.github.openminigameserver.gamecore.core.game.hosting

enum class GameHostingMode(val description: String, val isPrivate: Boolean = false) {
    PUBLIC("Public Game"),
    PRIVATE_PARTY("Private Game (Hosted by Party)", true),
    PRIVATE_ADMIN("Private Game (Hosted by Admin)", true)
}