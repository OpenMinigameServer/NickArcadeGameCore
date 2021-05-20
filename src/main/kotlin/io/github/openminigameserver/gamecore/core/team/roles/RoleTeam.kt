package io.github.openminigameserver.gamecore.core.team.roles

import io.github.openminigameserver.gamecore.core.players.currentTeam
import io.github.openminigameserver.gamecore.core.team.ColoredGameModeTeam
import io.github.openminigameserver.gamecore.core.team.SpectatorTeam
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.scoreboard.Team

class RoleTeam(
    name: String,
    friendlyName: String,
    gameMode: GameMode,
    maxPlayers: Int,
    color: NamedTextColor,
    val displayConfiguration: RoleTeamDisplayConfiguration,
    prefix: Component?,
    suffix: Component?
) : ColoredGameModeTeam(
    name, friendlyName,
    gameMode, Material.AIR, maxPlayers,
    color, prefix, suffix
) {

    override fun configureScoreboardTeam(team: Team, target: ArcadePlayer, viewer: ArcadePlayer) {
        //Only see players with same role, if role is allowed to see other player roles or if the viewer is a spectator
        if (viewer.currentTeam is SpectatorTeam || target.currentTeam == viewer.currentTeam || displayConfiguration.displayOtherRoles) {
            super.configureScoreboardTeam(team, target, viewer)
            return
        }

        team.apply {
            color(if (displayConfiguration.isOtherRolesFriendly) NamedTextColor.GREEN else NamedTextColor.RED)
            prefix(null)
            suffix(null)
        }

    }
}