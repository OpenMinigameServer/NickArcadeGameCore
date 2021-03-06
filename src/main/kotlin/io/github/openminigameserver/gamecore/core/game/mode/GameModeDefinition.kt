package io.github.openminigameserver.gamecore.core.game.mode

import io.github.openminigameserver.gamecore.core.arena.ArenaLocation
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.properties.*
import io.github.openminigameserver.gamecore.core.phases.GamePhase
import io.github.openminigameserver.gamecore.core.team.GameTeam
import java.util.*

open class GameModeDefinition(name: String, val friendlyName: String) : GameDefinitionPropertyContainer {
    var name = name
        internal set
    lateinit var game: GameDefinition
    var minimumPlayersToStart = 2
    var maximumPlayers = 2

    override val properties: MutableMap<GamePropertyType, MutableList<GamePropertyDefinition<*>>> = mutableMapOf()

    private val teams = mutableSetOf<() -> GameTeam>()
    private val phases = mutableSetOf<() -> GamePhase>()
    val modeTeams: Set<() -> GameTeam> = Collections.unmodifiableSet(teams)
    val modePhases: Set<() -> GamePhase> = Collections.unmodifiableSet(phases)

    val spawnLocation = prop<ArenaLocation>("spawnLocation", "Spawn Location", GamePropertyType.ARENA)

    fun addTeam(team: () -> GameTeam) {
        teams.add(team)
    }

    fun addPhase(team: () -> GamePhase) {
        phases.add(team)
    }

    protected inline fun <reified T : GameTeam> team(noinline code: () -> T) {
        return addTeam(code)
    }

    protected inline fun <reified T : GamePhase> phase(noinline code: () -> T) {
        return addPhase(code)
    }

    override fun toString(): String {
        return "${game.friendlyName} $friendlyName"
    }
}