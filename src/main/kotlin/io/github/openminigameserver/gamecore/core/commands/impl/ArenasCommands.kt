package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.arena.ArenaLocation
import io.github.openminigameserver.gamecore.core.arena.manager.ArenaManager
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.utils.InfoComponent
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.separator
import io.github.openminigameserver.nickarcade.plugin.extensions.command
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.RED

object ArenasCommands {
    private const val arenasCommandPrefix = "game <game> admin arenas"

    @CommandMethod("$arenasCommandPrefix create <mode> <name>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun createArena(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("name") name: String
    ) = command(sender) {

        val slimeWorld = sender.player?.world?.slimeWorld
        if (slimeWorld == null) {
            sender.audience.sendMessage(separator {
                append(text("You need to be in a slime world to create an arena from it!", RED))
            })
            return@command
        }

        val playerLocation = sender.player?.location?.let { ArenaLocation(it) } ?: return@command
        ArenaManager.saveArena(ArenaDefinition(mode, name, slimeWorld.name).apply {
            spawnLocation = playerLocation
        })
        sender.audience.sendMessage(text("Arena named $name was created for $mode.", GREEN))
    }

    @CommandMethod("$arenasCommandPrefix list <mode>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun listArenas(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
    ) = command(sender) {
        val arenas = ArenaManager.getArenasForGameModeDefinition(mode)
        if (arenas.isEmpty()) {
            sender.audience.sendMessage(separator {
                append(text("There are no arenas for $mode!", RED))
            })
        }
    }

    @CommandMethod("$arenasCommandPrefix info <mode> <arena>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun arenaInfo(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition
    ) = command(sender) {
        sender.audience.sendMessage(
            text {
                it.append(InfoComponent("Name", arena.name)).append(newline())
                it.append(InfoComponent("Id", arena.id)).append(newline())
                it.append(InfoComponent("World File Name", arena.worldFileName))
            }
        )
    }
    @CommandMethod("$arenasCommandPrefix remove <mode> <arena>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun removeArena(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition
    ) = command(sender) {
        ArenaManager.removeArena(arena)
        sender.audience.sendMessage(
            text("Removed arena $arena from $mode", GREEN)
        )
    }

}