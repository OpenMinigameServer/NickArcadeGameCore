package io.github.openminigameserver.gamecore.core.game.hosting.impl

import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingMode
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer

//Game is a public game and anyone can join
object PublicGameHostingInfo : GameHostingInfo() {
    override val mode: GameHostingMode
        get() = GameHostingMode.PUBLIC

    override fun canJoin(player: ArcadePlayer): Boolean = true
}