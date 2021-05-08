package io.github.openminigameserver.gamecore.core.game

import io.github.openminigameserver.gamecore.core.commands.GameCommandManager
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.gamecore.plugin.GameCorePlugin
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
        //Just for safety
        game.name = game.name.toUpperCase()

        registeredGamesMap[game.name] = (game)
        GameCorePlugin.instance.logger.info(
            "Registered game ${game.friendlyName} with game mode(s) [${
                game.gameModes.values.joinToString { it.friendlyName }
            }]"
        )
        GameCommandManager.registerCommands()
    }

    init {
    }
}