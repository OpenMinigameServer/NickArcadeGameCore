package io.github.openminigameserver.gamecore.core.game

import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import java.util.*

open class GameDefinition(val friendlyName: String, name: String) {

    var name = name
        internal set

    private val modes = mutableMapOf<String, GameModeDefinition>()
    val gameModes: Map<String, GameModeDefinition> = Collections.unmodifiableMap(modes)

    fun registerGameMode(gameMode: GameModeDefinition) {
        //Just for safety
        gameMode.name = gameMode.name.toUpperCase()

        gameMode.game = this
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