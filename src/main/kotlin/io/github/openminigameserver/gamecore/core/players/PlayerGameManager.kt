package io.github.openminigameserver.gamecore.core.players

import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.core.team.GameTeam
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.data.sender.player.extra.RuntimeExtraDataTag
import java.util.*

object PlayerGameManager {

    private val currentGames = mutableMapOf<UUID, GameInstance>()
    val games: Map<UUID, GameInstance> = Collections.unmodifiableMap(currentGames)

    fun getGameById(id: UUID): GameInstance? = currentGames[id]

    fun registerGame(game: GameInstance) {
        currentGames[game.id] = game
    }

    fun unregisterGame(game: GameInstance) {
        currentGames.remove(game.id)
    }

}

val currentGameTag = RuntimeExtraDataTag.of<UUID>("currentGame")

var ArcadePlayer.currentGame: GameInstance?
    get() = this[currentGameTag]?.let { PlayerGameManager.getGameById(it) }
    set(value) {
        this[currentGameTag] = value?.id
    }

var ArcadePlayer.currentTeam: GameTeam?
    get() = currentGame?.getPlayerTeam(this)
    set(value) {
        currentGame?.getPlayerTeam(this)?.removePlayer(this)
        value?.addPlayer(this)
    }