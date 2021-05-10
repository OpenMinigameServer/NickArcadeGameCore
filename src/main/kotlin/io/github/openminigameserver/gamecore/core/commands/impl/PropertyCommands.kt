package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.ui.ArenaPropertySelector
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank

object PropertyCommands {

    @CommandMethod("game <game> admin properties <mode> <arena>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameListProperties(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition,
    ) {
        val player = sender.player ?: return

        val propertySelector = ArenaPropertySelector(mode, arena)
        propertySelector.show(player)
    }
}