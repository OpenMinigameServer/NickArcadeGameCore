package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.core.game.GameState
import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingMode
import io.github.openminigameserver.gamecore.core.game.hosting.impl.AdminHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.impl.PartyHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.impl.PublicGameHostingInfo
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.utils.InfoComponent
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.separator
import io.github.openminigameserver.nickarcade.party.model.getOrCreateParty
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import net.kyori.adventure.text.format.NamedTextColor.GREEN


object InfoCommands {

    @CommandMethod("game <game> info")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameInfo(sender: ArcadeSender, @Argument("game") game: GameDefinition) {
        sender.audience.sendMessage(text {
            it.append(
                text(
                    "Game Name: ",
                    GREEN
                ).append(text(game.friendlyName))
            ).append(Component.newline())
            it.append(text("Internal name: ", GREEN).append(text(game.name)))
        })
    }

    @CommandMethod("game <game> debug worldinfo")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDebugWorldInfo(sender: ArcadePlayer, @Argument("game") game: GameDefinition) {
        val world = sender.player?.world ?: return
        val slimeWorld = world.slimeWorld
        sender.audience.sendMessage(text {
            it.append(text("Name: ", GREEN).append(text(world.name, GOLD))).append(newline())
            it.append(text("Is slime world: ", GREEN).append(text(slimeWorld != null, GOLD))).append(newline())
        })
    }

    @CommandMethod("game <game> debug create <mode> <arena> <hostingMode>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDebugMode(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition,
        @Argument("hostingMode") gameHostingMode: GameHostingMode,
    ) {

        val info = when (gameHostingMode) {
            GameHostingMode.PUBLIC -> PublicGameHostingInfo
            GameHostingMode.PRIVATE_PARTY -> PartyHostingInfo(sender.getOrCreateParty().id)
            GameHostingMode.PRIVATE_ADMIN -> AdminHostingInfo(sender)
        }

        GameInstance(game, mode, arena, info).also {
            it.addPlayer(sender)
        }

    }

    @CommandMethod("game <game> debug info")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameShowDebugInfo(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
    ) = gameCommand(sender, game) { currentGame ->

        val mode = currentGame.mode
        val gameHostingMode = currentGame.hostingInfo.mode

        sender.audience.sendMessage(text {
            it.append(InfoComponent("Internal Name", game.name)).append(newline())
            it.append(InfoComponent("Friendly Name", game.friendlyName)).append(newline())
            it.append(InfoComponent("Selected Mode Name", mode.friendlyName)).append(newline())
            it.append(InfoComponent("Selected Mode Internal Name", mode.name)).append(newline())

            it.append(InfoComponent("Selected Mode Teams", mode.modeTeams.joinToString { it().name })).append(newline())
            it.append(InfoComponent("State", currentGame.state)).append(newline())
            it.append(InfoComponent("Hosting Mode", gameHostingMode)).append(newline())
        })
    }


    @CommandMethod("game <game> setState <state>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameSetState(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("state") state: GameState
    ) = gameCommand(sender, game) { currentGame ->
        currentGame.state = state
        sender.audience.sendMessage(
            separator {
                append(text("Set game state to $state", GREEN))
            }
        )
    }
}