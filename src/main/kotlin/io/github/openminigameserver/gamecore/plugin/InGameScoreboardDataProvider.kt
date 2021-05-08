package io.github.openminigameserver.gamecore.plugin

import io.github.openminigameserver.gamecore.core.game.GameState
import io.github.openminigameserver.gamecore.core.phases.impl.LobbyPhase
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.display.scoreboard.ScoreboardDataProvider
import io.github.openminigameserver.nickarcade.display.scoreboard.SidebarData
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.ChatColor
import org.bukkit.scoreboard.Team
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object InGameScoreboardDataProvider : ScoreboardDataProvider {
    override suspend fun providePrefix(target: ArcadePlayer, viewer: ArcadePlayer): Component? {
        val currentGame = target.currentGame
        if (currentGame != null && currentGame.state == GameState.WAITING_FOR_PLAYERS) {
            return text(ChatColor.getLastColors(target.computeEffectivePrefix(false) ?: ""))
        }
        return null
    }

    override suspend fun provideTeamConfiguration(target: ArcadePlayer, viewer: ArcadePlayer, team: Team): Boolean {
        val currentGame = target.currentGame
        if (currentGame != null) {
            val playerTeam = currentGame.getPlayerTeam(target)
            if (currentGame.state != GameState.WAITING_FOR_PLAYERS) {
                playerTeam.configureScoreboardTeam(team, target, viewer)
                return true
            }
        }
        return false
    }

    override suspend fun provideSideBar(player: ArcadePlayer): SidebarData? {
        val game = player.currentGame ?: return null

        val lines = mutableListOf<Component>()

        lines.add(text(LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")), NamedTextColor.GRAY))
        lines.add(empty())
        lines.add(text("Map: ").append(text(game.arena.name, GREEN)))
        lines.add(text("Players: ").append(text("${game.playerCount}/${game.maxPlayerCount}", GREEN)))
        lines.add(empty())
        val currentPhase = game.currentPhase
        if (currentPhase is LobbyPhase) {
            if (currentPhase.shouldResetTimer()) {
                lines.add(text("Waiting..."))
            } else {
                lines.add(text("Starting in ").append(text("${currentPhase.remainingTime.inSeconds.toInt()}s", GREEN)))
            }
        }
        lines.add(empty())
        lines.add(text("Mode: ").append(text(game.mode.friendlyName, GREEN)))

        return SidebarData(
            text(game.game.friendlyName.toUpperCase(), NamedTextColor.YELLOW, TextDecoration.BOLD),
            lines
        )
    }
}