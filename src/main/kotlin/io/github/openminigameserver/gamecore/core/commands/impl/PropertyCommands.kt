package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.utils.InfoComponent
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.separator
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank
import net.kyori.adventure.text.Component

object PropertyCommands {

    @CommandMethod("game <game> properties <mode> <arena> list")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameListProperties(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition,
    ) {
        sender.audience.sendMessage(separator {
            mode.properties.forEach { (type, props) ->
                append(InfoComponent("Type", type.name)).append(Component.newline())
                props.forEach { prop ->
                    append(InfoComponent("Internal Name", prop.name)).append(Component.newline())
                }
            }
        })
    }


}