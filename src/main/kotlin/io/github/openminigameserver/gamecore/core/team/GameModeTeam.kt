package io.github.openminigameserver.gamecore.core.team

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import org.bukkit.GameMode
import org.bukkit.Material

open class GameModeTeam(name: String, friendlyName: String, val gameMode: GameMode, selectorMaterial: Material, maxPlayers: Int) : GameTeam(
    name,
    friendlyName,
    selectorMaterial, maxPlayers
) {
    override fun onPlayerAdd(p: ArcadePlayer) {
        super.onPlayerAdd(p)
        p.player?.gameMode = gameMode
    }
}