package io.github.openminigameserver.gamecore.core.phases.impl

import io.github.openminigameserver.gamecore.core.game.GameState
import io.github.openminigameserver.gamecore.core.phases.TimedPhase
import io.github.openminigameserver.gamecore.core.phases.disablePlayerDamage
import io.github.openminigameserver.gamecore.core.phases.disablePlayerHunger
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import kotlin.time.seconds

const val lobbyWaitingTime = 15

class LobbyPhase : TimedPhase("lobbyPhase", "Lobby", lobbyWaitingTime.seconds) {

    init {
        disablePlayerDamage()
        disablePlayerHunger()
    }

    override suspend fun onEnd() {
        game.lobbyTeam.applyTeamSelections()
        game.state = GameState.PLAYING
    }

    override suspend fun onStart() {
        game.state = GameState.WAITING_FOR_PLAYERS
        super.onStart()
    }

    override suspend fun shouldResetTimer(): Boolean {
        val playerCount = game.playerCount

        return !game.isDeveloperGame && (playerCount < game.mode.minimumPlayersToStart || playerCount == 0)
    }

    override suspend fun onTick() {
        val time = remainingTime.inSeconds.toInt().coerceAtLeast(1)
        val color = when {
            time <= 5 -> RED
            time <= 10 -> YELLOW
            else -> GREEN
        }
        val shouldSend = time == 15 || time == 10 || time <= 5
        if (shouldSend) {
            game.audience.sendMessage(
                text(
                    "The game starts in ", YELLOW
                ).append(
                    text(
                        time,
                        color
                    ).append(
                        text(" second${if (time == 1) "" else "s"}!", YELLOW)
                    )
                )
            )
        }
    }
}