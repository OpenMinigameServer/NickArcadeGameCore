package io.github.openminigameserver.gamecore.core.game

import java.util.*

object GameManager {

    private val registeredGamesMap = mutableMapOf<String, GameDefinition>()
    val registeredGames: MutableMap<String, GameDefinition> = Collections.unmodifiableMap(registeredGamesMap)

    fun registerGame(game: GameDefinition) {
        registeredGamesMap[game.name] = (game)
    }

    init {
    }
}