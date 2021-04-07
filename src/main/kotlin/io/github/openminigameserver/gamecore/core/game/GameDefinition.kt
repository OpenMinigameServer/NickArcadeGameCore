package io.github.openminigameserver.gamecore.core.game

import io.github.openminigameserver.gamecore.core.game.mode.MiniGameMode
import java.util.*

open class GameDefinition(val friendlyName: String, val name: String) {
    private val modes = mutableMapOf<String, MiniGameMode>()
    val gameModes: Map<String, MiniGameMode> = Collections.unmodifiableMap(modes)

    fun registerGameMode(gameMode: MiniGameMode) {
        modes[gameMode.name] = gameMode
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameDefinition) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}