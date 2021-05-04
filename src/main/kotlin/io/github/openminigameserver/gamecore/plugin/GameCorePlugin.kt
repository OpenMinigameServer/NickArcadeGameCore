package io.github.openminigameserver.gamecore.plugin

import io.github.openminigameserver.gamecore.core.arena.manager.io.json.GameCoreModule
import io.github.openminigameserver.gamecore.core.commands.GameCommandManager
import io.github.openminigameserver.nickarcade.core.io.database.helpers.MongoDbConnectionHelper
import io.github.openminigameserver.nickarcade.display.managers.ScoreboardDataProviderManager
import org.bukkit.plugin.java.JavaPlugin

class GameCorePlugin : JavaPlugin() {
    companion object {
        val instance: GameCorePlugin
            get() = getPlugin(GameCorePlugin::class.java)
    }

    override fun onEnable() {
        MongoDbConnectionHelper.registerModule(GameCoreModule)
        ScoreboardDataProviderManager.registerProvider(InGameScoreboardDataProvider)
        GameCommandManager.registerCommands()
    }
}