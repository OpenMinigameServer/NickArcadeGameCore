package io.github.openminigameserver.gamecore.core.team

import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import org.bukkit.Material
import org.bukkit.scoreboard.Team
import java.util.*

abstract class GameTeam(var name: String, var selectorMaterial: Material, var maxPlayers: Int) {
    lateinit var game: GameInstance

    private val playerSet: MutableSet<ArcadePlayer> = mutableSetOf()
    val players: Set<ArcadePlayer> = Collections.unmodifiableSet(playerSet)

    fun addPlayer(p: ArcadePlayer) {
        if (playerSet.add(p)) {
            onPlayerAdd(p)
        }
    }

    fun removePlayer(p: ArcadePlayer) {
        if (playerSet.remove(p)) {
            onPlayerRemove(p)
        }
    }

    open fun configureScoreboardTeam(team: Team) {}

    open fun onPlayerAdd(p: ArcadePlayer) {}

    open fun onPlayerRemove(p: ArcadePlayer) {}
}