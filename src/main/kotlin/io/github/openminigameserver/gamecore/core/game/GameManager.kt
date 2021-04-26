package io.github.openminigameserver.gamecore.core.game

import io.github.openminigameserver.gamecore.core.commands.GameCommandManager
import java.util.*

object GameManager {

    private val registeredGamesMap = mutableMapOf<String, GameDefinition>()
    val registeredGames: MutableMap<String, GameDefinition> = Collections.unmodifiableMap(registeredGamesMap)

    fun registerGame(game: GameDefinition) {
        registeredGamesMap[game.name] = (game)
        GameCommandManager.registerCommands()
    }

    init {
    }
}