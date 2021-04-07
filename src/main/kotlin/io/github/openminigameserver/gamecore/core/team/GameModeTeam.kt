package io.github.openminigameserver.gamecore.core.team

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import org.bukkit.GameMode
import org.bukkit.Material

abstract class GameModeTeam(name: String, val gameMode: GameMode, selectorMaterial: Material) : GameTeam(name,
    selectorMaterial
) {
    override fun onPlayerAdd(p: ArcadePlayer) {
        super.onPlayerAdd(p)
        p.player?.gameMode = gameMode
    }
}