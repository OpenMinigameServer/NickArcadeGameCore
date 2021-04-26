package io.github.openminigameserver.gamecore.core.game

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import com.grinderwolf.swm.api.SlimePlugin
import com.grinderwolf.swm.api.world.properties.SlimeProperties
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap
import com.grinderwolf.swm.plugin.SWMPlugin
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingInfo
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.players.PlayerGameManager
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.gamecore.core.team.GameTeam
import io.github.openminigameserver.gamecore.core.team.LobbyTeam
import io.github.openminigameserver.gamecore.core.team.SpectatorTeam
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.display.managers.ScoreboardManager
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.World
import java.util.*


@JsonIdentityInfo(property = "_id", generator = ObjectIdGenerators.PropertyGenerator::class)
data class GameInstance(
    val game: GameDefinition,
    val mode: GameModeDefinition,
    val arena: ArenaDefinition,
    var hostingInfo: GameHostingInfo,
    var state: GameState = GameState.WAITING_FOR_PLAYERS,
    @JsonProperty("_id") val id: UUID = UUID.randomUUID()
) {
    val audience = GameAudience(this)
    val spectatorTeam = SpectatorTeam().also { it.game = this }
    val lobbyTeam = LobbyTeam().also { it.game = this }

    val teams = mode.modeTeams.map { it().also { it.game = this } } + spectatorTeam + lobbyTeam

    init {
        PlayerGameManager.registerGame(this)
    }

    val worldArena = loadArena()
    val respawnLocation: Location = arena.spawnLocation.toLocation(worldArena)

    private fun loadArena(): World {
        val finalWorldName = UUID.randomUUID().toString().replace("-", "")
        val slimeManager = SWMPlugin.getInstance() as SlimePlugin
        val fileLoader = slimeManager.getLoader("file")

        val clonedWorld = slimeManager.loadWorld(fileLoader, arena.worldFileName, true,
            createPropertyMap()
        ).clone(finalWorldName) //Load original world, then clone it
        slimeManager.generateWorld(clonedWorld)

        return Bukkit.getWorld(finalWorldName)!!
    }

    private fun createPropertyMap() = SlimePropertyMap().apply {
        setValue(SlimeProperties.ALLOW_ANIMALS, false)
        setValue(SlimeProperties.ALLOW_MONSTERS, false)
        setValue(SlimeProperties.DIFFICULTY, "easy")
        setValue(SlimeProperties.SPAWN_X, arena.spawnLocation.x.toInt())
        setValue(SlimeProperties.SPAWN_Y, arena.spawnLocation.y.toInt())
        setValue(SlimeProperties.SPAWN_Z, arena.spawnLocation.z.toInt())
    }

    fun addPlayer(player: ArcadePlayer): Boolean {
        player.player?.teleport(arena.spawnLocation.toLocation(worldArena))
        player.currentGame = this
        player.player?.let { launch { ScoreboardManager.refreshScoreboard(it) } }
        val canJoin = hostingInfo.canJoin(player)
        if (canJoin) {
            lobbyTeam.addPlayer(player)
        }
        return canJoin
    }

    fun getPlayerTeam(player: ArcadePlayer): GameTeam {
        return teams.firstOrNull { it.players.contains(player) } ?: lobbyTeam.also {
            it.addPlayer(player)
        }
    }
}