package io.github.openminigameserver.gamecore.core.phases

import io.github.openminigameserver.gamecore.core.game.GameState
import kotlin.time.seconds

class GameEndPhase : TimedPhase("gameEnd", "Game End", 10.seconds) {
    override suspend fun onStart() {
        game.state = GameState.GAME_END
        super.onStart()
    }

    override suspend fun onEnd() {
        game.close()
    }
}