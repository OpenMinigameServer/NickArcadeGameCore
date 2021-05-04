package io.github.openminigameserver.gamecore.core.team

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.scoreboard.Team

open class ColoredGameModeTeam(
    name: String, friendlyName: String,
    gameMode: GameMode,
    selectorMaterial: Material,
    maxPlayers: Int,
    var color: NamedTextColor, var prefix: Component? = null, var suffix: Component? = null
) : GameModeTeam(name, friendlyName, gameMode, selectorMaterial, maxPlayers) {

    override fun configureScoreboardTeam(team: Team, target: ArcadePlayer, viewer: ArcadePlayer) {
        super.configureScoreboardTeam(team, target, viewer)
        team.apply {
            color(this@ColoredGameModeTeam.color)
            prefix(this@ColoredGameModeTeam.prefix?.append(Component.space()))
            suffix(this@ColoredGameModeTeam.suffix)
        }
    }
}