package io.github.openminigameserver.gamecore.testgame

import io.github.openminigameserver.gamecore.core.game.GameDefinition

class TestGameDefinition : GameDefinition("Test", "TEST") {
    init {
        registerGameMode(SoloTestMode())
    }
}