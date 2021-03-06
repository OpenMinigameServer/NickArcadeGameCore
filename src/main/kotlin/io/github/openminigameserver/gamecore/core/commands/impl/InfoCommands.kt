package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingMode
import io.github.openminigameserver.gamecore.core.game.hosting.impl.AdminHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.impl.PartyHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.impl.PublicBedrockGameHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.impl.PublicGameHostingInfo
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.phases.TimedPhase
import io.github.openminigameserver.gamecore.core.phases.impl.LobbyPhase
import io.github.openminigameserver.gamecore.utils.InfoComponent
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.party.model.getCurrentParty
import io.github.openminigameserver.nickarcade.party.model.getOrCreateParty
import io.github.openminigameserver.nickarcade.plugin.extensions.command
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import kotlin.time.Duration

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
            GameHostingMode.PUBLIC_BEDROCK -> PublicBedrockGameHostingInfo
        }

        val party = sender.getCurrentParty(false)
        val canJoin = party == null || party.isLeader(sender)
        if (canJoin) {
            GameInstance(game, mode, arena, info).also { game ->
                game.loadArena()
                game.startPhasesTimer()
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

            it.append(InfoComponent("Selected Mode Teams", mode.modeTeams.joinToString { it().friendlyName })).append(newline())
            it.append(InfoComponent("State", currentGame.state)).append(newline())
            it.append(InfoComponent("Hosting Mode", gameHostingMode)).append(newline())
        })
    }


    @CommandMethod("game <game> phase skip")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameSkipCurrentPhase(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
    ) = gameCommand(sender, game) { currentGame ->
        sender.audience.sendMessage(
            text("Skipped current phase ", GREEN).append(text(currentGame.currentPhase.friendlyName, GOLD))
        )
        currentGame.phasesTimer.skipCurrent = true
    }

    @CommandMethod("game <game> phase setElapsed <time>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDurationSetElapsed(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("time") seconds: Int
    ) = gameCommand(sender, game) { currentGame ->
        val currentPhase = currentGame.currentPhase
        if (currentPhase !is TimedPhase) {
            sender.audience.sendMessage(
                text("Unable to set phase elapsed time! The current phase is not time-based.", RED)
            )
            return@gameCommand
        }
        val finalTime = Duration.seconds(seconds)
        sender.audience.sendMessage(
            text("Successfully current phase's elapsed time to ", GREEN).append(text(finalTime.toString(), GOLD))
        )
        currentPhase.elapsedTime = finalTime
    }

    @CommandMethod("game <game> phase setRemaining <time>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDurationSetRemaining(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("time") seconds: Int
    ) = gameCommand(sender, game) { currentGame ->
        val currentPhase = currentGame.currentPhase
        if (currentPhase !is TimedPhase) {
            sender.audience.sendMessage(
                text("Unable to set phase remaining time! The current phase is not time-based.", RED)
            )
            return@gameCommand
        }
        val time = Duration.seconds(seconds)
        val finalTime = currentPhase.duration - Duration.seconds(seconds)
        sender.audience.sendMessage(
            text("Successfully current phase's remaining time to ", GREEN).append(text(time.toString(), GOLD))
        )
        currentPhase.elapsedTime = finalTime
    }

    @CommandMethod("game <game> dispose")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDispose(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition
    ) = gameCommand(sender, game) { currentGame ->
        currentGame.close()
    }

    @CommandMethod("game <game> forceStart")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameDurationSetElapsed(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
    ) = gameCommand(sender, game) { currentGame ->
        val currentPhase = currentGame.currentPhase
        if (currentPhase !is LobbyPhase) {
            sender.audience.sendMessage(
                text("Unable to force a game start when the game is already running.", RED)
            )
            return@gameCommand
        }
        currentPhase.forceStart = true
        sender.audience.sendMessage(
            text("Successfully forced the game to start.", GREEN)
        )
    }
}