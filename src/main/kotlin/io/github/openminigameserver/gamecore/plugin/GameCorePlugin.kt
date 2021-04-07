package io.github.openminigameserver.gamecore.plugin

import io.github.openminigameserver.gamecore.core.commands.GameCommandManager
import io.github.openminigameserver.gamecore.core.game.GameManager
import io.github.openminigameserver.gamecore.testgame.TestGameDefinition
import io.github.openminigameserver.nickarcade.display.managers.ScoreboardDataProviderManager
import org.bukkit.plugin.java.JavaPlugin

class GameCorePlugin : JavaPlugin() {
    companion object {
        val instance: GameCorePlugin
            get() = getPlugin(GameCorePlugin::class.java)
    }

    override fun onEnable() {
        ScoreboardDataProviderManager.registerProvider(InGameScoreboardDataProvider)
        GameManager.registerGame(TestGameDefinition())
        GameCommandManager.registerCommands()
    }
}