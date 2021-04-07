package io.github.openminigameserver.gamecore.core.game.mode

import io.github.openminigameserver.gamecore.core.team.GameTeam
import java.util.*

open class MiniGameMode(val name: String, val friendlyName: String) {
    var minimumPlayersToStart = 1
    var maximumPlayers = 2

    private val teams = mutableSetOf<() -> GameTeam>()
    val modeTeams: Set<() -> GameTeam> = Collections.unmodifiableSet(teams)

    fun addTeam(team: () -> GameTeam) {
        teams.add(team)
    }
}