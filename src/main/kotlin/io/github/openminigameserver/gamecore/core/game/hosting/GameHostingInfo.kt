package io.github.openminigameserver.gamecore.core.game.hosting

import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer

abstract class GameHostingInfo {
    abstract val mode: GameHostingMode

    abstract fun canJoin(player: ArcadePlayer): Boolean
}