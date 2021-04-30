package io.github.openminigameserver.gamecore.core.phases

import io.github.openminigameserver.gamecore.core.game.GameInstance

abstract class GamePhase(var name: String, var friendlyName: String) {
    lateinit var game: GameInstance

    abstract fun onStart()

    abstract fun onEnd()

    abstract fun shouldEnd(): Boolean
}