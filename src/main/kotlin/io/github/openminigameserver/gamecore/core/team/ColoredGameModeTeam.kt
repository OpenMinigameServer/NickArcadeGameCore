package io.github.openminigameserver.gamecore.core.team

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.scoreboard.Team

open class ColoredGameModeTeam(
    name: String,
    gameMode: GameMode,
    selectorMaterial: Material,
    maxPlayers: Int,
    var color: NamedTextColor, var prefix: Component? = null, var suffix: Component? = null
) : GameModeTeam(name, gameMode, selectorMaterial, maxPlayers) {

    override fun configureScoreboardTeam(team: Team) {
        super.configureScoreboardTeam(team)
        team.apply {
            color(this@ColoredGameModeTeam.color)
            prefix(this@ColoredGameModeTeam.prefix?.append(Component.space()))
            suffix(this@ColoredGameModeTeam.suffix)
        }
    }
}