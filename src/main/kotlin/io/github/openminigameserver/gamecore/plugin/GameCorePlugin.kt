package io.github.openminigameserver.gamecore.plugin

import io.github.openminigameserver.gamecore.core.arena.manager.io.json.GameCoreModule
import io.github.openminigameserver.gamecore.core.commands.GameCommandManager
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.nickarcade.core.io.database.helpers.MongoDbConnectionHelper
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.display.managers.ScoreboardDataProviderManager
import io.github.openminigameserver.nickarcade.plugin.extensions.event
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.plugin.java.JavaPlugin

class GameCorePlugin : JavaPlugin() {
    companion object {
        val instance: GameCorePlugin
            get() = getPlugin(GameCorePlugin::class.java)
    }

    override fun onEnable() {
        event<PlayerRespawnEvent>(forceBlocking = true) {
            val currentGame = player.getArcadeSender().currentGame ?: return@event
            this.respawnLocation = currentGame.respawnLocation
        }
        MongoDbConnectionHelper.registerModule(GameCoreModule)
        ScoreboardDataProviderManager.registerProvider(InGameScoreboardDataProvider)
        GameCommandManager.registerCommands()
    }
}