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
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.separator
import io.github.openminigameserver.nickarcade.party.model.getCurrentParty
import io.github.openminigameserver.nickarcade.party.model.getOrCreateParty
import io.github.openminigameserver.nickarcade.plugin.extensions.command
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN

object InfoCommands {

    @CommandMethod("game <game> admin create <mode> <arena> <hostingMode>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDebugMode(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition,
        @Argument("hostingMode") gameHostingMode: GameHostingMode,
    ) = command(sender) {

        val info = when (gameHostingMode) {
            GameHostingMode.PUBLIC -> PublicGameHostingInfo
            GameHostingMode.PRIVATE_PARTY -> PartyHostingInfo(sender.getOrCreateParty().id)
            GameHostingMode.PRIVATE_ADMIN -> AdminHostingInfo(sender)
        }

        val party = sender.getCurrentParty(false)
        val canJoin = party == null || party.isLeader(sender)
        if (canJoin) {
            GameInstance(game, mode, arena, info).also { game ->
                game.loadArena()
                if (party != null) {
                    party.membersList.forEach {
                        game.addPlayer(it.player)
                    }
                } else {
                    game.addPlayer(sender)
                }
            }
        }
    }

    @CommandMethod("game <game> admin info")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameShowDebugInfo(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
    ) = gameCommand(sender, game) { currentGame ->

        val mode = currentGame.mode
        val gameHostingMode = currentGame.hostingInfo.mode

        sender.audience.sendMessage(text {
            it.append(InfoComponent("Friendly Name", game.friendlyName)).append(newline())
            it.append(InfoComponent("Selected Mode Name", mode.friendlyName)).append(newline())

            it.append(InfoComponent("Selected Mode Teams", mode.modeTeams.joinToString { it().name })).append(newline())
            it.append(InfoComponent("State", currentGame.state)).append(newline())
            it.append(InfoComponent("Hosting Mode", gameHostingMode)).append(newline())
        })
    }


    @CommandMethod("game <game> admin state <state>")
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

    @CommandMethod("game <game> admin dispose")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDispose(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition
    ) = gameCommand(sender, game) { currentGame ->
        currentGame.close()
    }
}