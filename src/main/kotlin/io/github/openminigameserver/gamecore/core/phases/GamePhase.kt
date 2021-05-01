package io.github.openminigameserver.gamecore.core.phases

import io.github.openminigameserver.gamecore.core.game.GameInstance

abstract class GamePhase(var name: String, var friendlyName: String) {
    lateinit var game: GameInstance

    abstract suspend fun onStart()

    abstract suspend fun onEnd()

    open suspend fun onTick() {}

    /**
     * This method is called on a timer every second.
     */
    abstract suspend fun shouldEnd(): Boolean
}