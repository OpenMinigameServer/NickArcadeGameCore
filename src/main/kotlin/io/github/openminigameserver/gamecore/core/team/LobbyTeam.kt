package io.github.openminigameserver.gamecore.core.team

import org.bukkit.GameMode
import org.bukkit.Material

class LobbyTeam : GameModeTeam(
    "Lobby", GameMode.ADVENTURE, Material.AIR
)