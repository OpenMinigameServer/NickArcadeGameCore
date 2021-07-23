package io.github.openminigameserver.gamecore.core.phases.impl

import io.github.openminigameserver.gamecore.core.game.GameState
import io.github.openminigameserver.gamecore.core.phases.TimedPhase
import kotlin.time.Duration
import kotlin.time.seconds

class GameEndPhase : TimedPhase("gameEnd", "Game End", Duration.seconds(10)) {
    override suspend fun onStart() {
        game.state = GameState.GAME_END
        super.onStart()
    }

    override suspend fun onEnd() {
        game.close()
    }
}