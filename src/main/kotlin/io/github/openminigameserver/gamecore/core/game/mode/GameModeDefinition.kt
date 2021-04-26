package io.github.openminigameserver.gamecore.core.game.mode

import io.github.openminigameserver.gamecore.core.arena.ArenaLocation
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyContainer
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyType
import io.github.openminigameserver.gamecore.core.game.properties.prop
import io.github.openminigameserver.gamecore.core.team.GameTeam
import java.util.*

open class GameModeDefinition(val name: String, val friendlyName: String) : GamePropertyContainer {
    lateinit var game: GameDefinition
    var minimumPlayersToStart = 2
    var maximumPlayers = 2

    override val properties: MutableMap<GamePropertyType, MutableList<GamePropertyDefinition<*>>> = mutableMapOf()

    private val teams = mutableSetOf<() -> GameTeam>()
    val modeTeams: Set<() -> GameTeam> = Collections.unmodifiableSet(teams)

    val spawnLocation = prop<ArenaLocation>("spawnLocation", GamePropertyType.ARENA)

    fun addTeam(team: () -> GameTeam) {
        teams.add(team)
    }

    protected inline fun <reified T : GameTeam> team(code: () -> T): () -> T {
        return code.also { addTeam(it) }
    }

    override fun toString(): String {
        return "${game.friendlyName} $friendlyName"
    }


}