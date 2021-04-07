package io.github.openminigameserver.gamecore.plugin

import io.github.openminigameserver.gamecore.core.game.GameState
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.display.scoreboard.ScoreboardDataProvider
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.text
import org.bukkit.ChatColor
import org.bukkit.scoreboard.Team

object InGameScoreboardDataProvider : ScoreboardDataProvider {
    override suspend fun providePrefix(player: ArcadePlayer): Component? {
        val currentGame = player.currentGame
        if (currentGame != null && currentGame.state == GameState.WAITING_FOR_PLAYERS) {
            return text(ChatColor.getLastColors(player.computeEffectivePrefix(false) ?: ""))
        }
        return null
    }

    override suspend fun provideTeamConfiguration(player: ArcadePlayer, team: Team): Boolean {
        val currentGame = player.currentGame
        if (currentGame != null) {
            val playerTeam = currentGame.getPlayerTeam(player)
            if (currentGame.state != GameState.WAITING_FOR_PLAYERS) {
                playerTeam.configureScoreboardTeam(team)
                return true
            }
        }
        return false
    }
}