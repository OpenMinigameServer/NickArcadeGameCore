package io.github.openminigameserver.gamecore.core.game

import com.fasterxml.jackson.annotation.JsonIdentityInfo
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.ObjectIdGenerators
import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingInfo
import io.github.openminigameserver.gamecore.core.game.mode.MiniGameMode
import io.github.openminigameserver.gamecore.core.players.PlayerGameManager
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.gamecore.core.team.GameTeam
import io.github.openminigameserver.gamecore.core.team.LobbyTeam
import io.github.openminigameserver.gamecore.core.team.SpectatorTeam
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.display.managers.ScoreboardManager
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import net.kyori.adventure.audience.Audience
import java.util.*


@JsonIdentityInfo(property = "_id", generator = ObjectIdGenerators.PropertyGenerator::class)
data class GameInstance(
    val game: GameDefinition,
    val mode: MiniGameMode,
    var hostingInfo: GameHostingInfo,
    var state: GameState = GameState.WAITING_FOR_PLAYERS,
    @JsonProperty("_id") val id: UUID = UUID.randomUUID()
) {
    val audience = GameAudience(this)
    val spectatorTeam = SpectatorTeam()
    val lobbyTeam = LobbyTeam()

    val teams = mode.modeTeams.map { it() } + spectatorTeam + lobbyTeam

    init {
        PlayerGameManager.registerGame(this)
    }

    fun addPlayer(player: ArcadePlayer): Boolean {
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