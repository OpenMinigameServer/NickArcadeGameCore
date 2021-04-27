package io.github.openminigameserver.gamecore.core.game

import io.github.openminigameserver.gamecore.core.commands.GameCommandManager
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.nickarcade.core.ui.ItemActionHelper
import io.github.openminigameserver.nickarcade.plugin.extensions.pluginInstance
import org.bukkit.NamespacedKey
import java.util.*

object GameManager {
    val teamSelectorAction = ItemActionHelper.registerAction(NamespacedKey(pluginInstance, "team_selector")) {
        it.currentGame?.openTeamSelectorMenu(it)
    }

    private val registeredGamesMap = mutableMapOf<String, GameDefinition>()
    val registeredGames: MutableMap<String, GameDefinition> = Collections.unmodifiableMap(registeredGamesMap)

    fun registerGame(game: GameDefinition) {
        registeredGamesMap[game.name] = (game)
        GameCommandManager.registerCommands()
    }

    init {
    }
}