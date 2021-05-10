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
import io.github.openminigameserver.gamecore.core.game.hosting.impl.PartyHostingInfo
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.mode.RoleGameModeDefinition
import io.github.openminigameserver.gamecore.core.phases.PhasesTimer
import io.github.openminigameserver.gamecore.core.phases.impl.GameEndPhase
import io.github.openminigameserver.gamecore.core.phases.impl.LobbyPhase
import io.github.openminigameserver.gamecore.core.players.GameInstanceManager
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.gamecore.core.team.GameTeam
import io.github.openminigameserver.gamecore.core.team.LobbyTeam
import io.github.openminigameserver.gamecore.core.team.SpectatorTeam
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.display.managers.ScoreboardManager
import io.github.openminigameserver.nickarcade.plugin.extensions.async
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import io.github.openminigameserver.nickarcade.plugin.extensions.sync
import io.github.openminigameserver.nickarcade.plugin.extensions.worldBoundEvent
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.AQUA
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import org.bukkit.Bukkit
import org.bukkit.GameRule
import org.bukkit.Location
import org.bukkit.World
import org.bukkit.event.player.PlayerRespawnEvent
import java.io.Closeable
import java.util.*
import kotlin.collections.ArrayDeque


@JsonIdentityInfo(property = "_id", generator = ObjectIdGenerators.PropertyGenerator::class)
data class GameInstance(
    val game: GameDefinition,
    val mode: GameModeDefinition,
    val arena: ArenaDefinition,
    var hostingInfo: GameHostingInfo,
    var state: GameState = GameState.WAITING_FOR_PLAYERS,
    @JsonProperty("_id") val id: UUID = UUID.randomUUID()
) : Closeable {
    val maxPlayerCount: Int = mode.maximumPlayers
    val audience = GameAudience(this)
    private val spectatorTeam = SpectatorTeam()
    internal val lobbyTeam = LobbyTeam()
    private val lobbyPhase = LobbyPhase()
    private val gameEndPhase = GameEndPhase()
    val teams = mode.modeTeams.map { it() }
    val allTeams =
        (teams + spectatorTeam + lobbyTeam).onEach { it.game = this@GameInstance }

    val playerCount: Int
        get() = allTeams.filterNot { it == spectatorTeam }.sumBy { it.players.size }

    val phases =
        ArrayDeque(listOf(lobbyPhase) + mode.modePhases.map { it() } + gameEndPhase).onEach {
            it.game = this@GameInstance
        }

    val currentPhase
        get() = phases.firstOrNull() ?: gameEndPhase

    internal val phasesTimer = PhasesTimer(this)

    init {
        GameInstanceManager.registerGame(this)
    }

    lateinit var worldArena: World
    val respawnLocation: Location get() = arena.spawnLocation.toLocation(worldArena)

    suspend fun loadArena() {
        val finalWorldName = UUID.randomUUID().toString().replace("-", "")
        val slimeManager = SWMPlugin.getInstance() as SlimePlugin
        val fileLoader = slimeManager.getLoader("file")

        val clonedWorld = async {
            slimeManager.loadWorld(
                fileLoader, arena.worldFileName, true,
                createPropertyMap()
            ).clone(finalWorldName) //Load original world, then clone it
        }
        sync { slimeManager.generateWorld(clonedWorld) }

        worldArena = async { Bukkit.getWorld(finalWorldName)!! }.apply {
            setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
            setGameRule(GameRule.DO_MOB_SPAWNING, false)
            setGameRule(GameRule.DO_WEATHER_CYCLE, false)
        }

        worldBoundEvent<PlayerRespawnEvent>(worldArena, forceBlocking = true) {
            val currentGame = player.getArcadeSender().currentGame ?: return@worldBoundEvent
            this.respawnLocation = currentGame.respawnLocation
        }
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
            audience.sendMessage(text {
                it.append(text(player.getChatName(actualData = false, colourPrefixOnly = true)))
                it.append(text(" has joined (", YELLOW))
                it.append(text(playerCount, AQUA))
                it.append(text("/", YELLOW))
                it.append(text(mode.maximumPlayers, AQUA))
                it.append(text(")!", YELLOW))
            })
        }
        return canJoin
    }

    fun getPlayerTeam(player: ArcadePlayer): GameTeam {
        return allTeams.firstOrNull { it.players.contains(player) } ?: lobbyTeam.also {
            it.addPlayer(player)
        }
    }

    fun openTeamSelectorMenu(player: ArcadePlayer) {
        lobbyTeam.openTeamSelectorMenu(player)
    }

    override fun close() {
        phasesTimer.stop()
        GameInstanceManager.unregisterGame(this)
        allTeams.forEach {
            it.players.asSequence().forEach { p ->
                p.player?.teleport(Bukkit.getWorlds().first().spawnLocation)
                it.removePlayer(p)
                p.currentGame = null
            }
        }
        Bukkit.unloadWorld(worldArena, false)
    }

    fun startPhasesTimer() {
        phasesTimer.launch()
    }

    val isDeveloperGame get() = hostingInfo is PartyHostingInfo && (hostingInfo as PartyHostingInfo).party?.isDeveloperGameParty() == true

    val isPrivateGame get() = hostingInfo is PartyHostingInfo && (hostingInfo as PartyHostingInfo).party?.isPrivateGameParty() == true

    val hostParty get() = (hostingInfo as? PartyHostingInfo)?.party

    fun isHostPartyLeader(player: ArcadePlayer) = hostParty?.isLeader(player) ?: false

    val isRolePlayGame get() = mode is RoleGameModeDefinition
}