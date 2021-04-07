package io.github.openminigameserver.gamecore.core.team

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.GameMode
import org.bukkit.Material

class SpectatorTeam : ColoredGameModeTeam(
    "Spectator", GameMode.SPECTATOR, Material.COMPASS, NamedTextColor.GOLD,
    Component.text("SPEC", NamedTextColor.GOLD, TextDecoration.BOLD)
)