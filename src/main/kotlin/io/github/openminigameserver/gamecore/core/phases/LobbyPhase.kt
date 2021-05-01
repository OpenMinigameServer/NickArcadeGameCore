package io.github.openminigameserver.gamecore.core.phases

import io.github.openminigameserver.gamecore.core.game.GameState
import io.github.openminigameserver.gamecore.core.game.hosting.impl.PartyHostingInfo
import kotlin.time.seconds

const val lobbyWaitingTime = 20
class LobbyPhase : TimedPhase("lobbyPhase", "Lobby", lobbyWaitingTime.seconds) {

    override suspend fun onEnd() {
        game.lobbyTeam.applyTeamSelections()
        game.state = GameState.PLAYING
    }

    override suspend fun onStart() {
        game.state = GameState.WAITING_FOR_PLAYERS
        super.onStart()
    }

    override suspend fun shouldResetTimer(): Boolean {
        val hostingInfo = game.hostingInfo
        val playerCount = game.playerCount

        val isDeveloperGame = hostingInfo is PartyHostingInfo && hostingInfo.party?.isDeveloperGameParty() == true
        return !isDeveloperGame && (playerCount < game.mode.minimumPlayersToStart || playerCount == 0)
    }
}