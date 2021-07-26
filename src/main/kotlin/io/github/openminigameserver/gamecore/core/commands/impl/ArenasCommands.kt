package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.arena.ArenaLocation
import io.github.openminigameserver.gamecore.core.arena.manager.ArenaManager
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyType
import io.github.openminigameserver.gamecore.core.team.GameTeam
import io.github.openminigameserver.gamecore.core.team.selector.displayComponent
import io.github.openminigameserver.gamecore.utils.InfoComponent
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.separator
import io.github.openminigameserver.nickarcade.plugin.extensions.clickEvent
import io.github.openminigameserver.nickarcade.plugin.extensions.command
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.newline
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.event.ClickEvent
import net.kyori.adventure.text.format.NamedTextColor.*
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

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
            return@command
        }

        sender.audience.sendMessage(
            text {
                it.append(text("Available arenas: ", GOLD))
                arenas.forEach { arena ->
                    it.append(newline()).append(
                        text(arena.name, if (arena.isValid) GREEN else RED).clickEvent(
                            ClickEvent.runCommand(
                                "/$arenasCommandPrefix info ${mode.name.lowercase(Locale.getDefault())} ${arena.name}".replace(
                                    "<game>",
                                    game.name.lowercase(Locale.getDefault())
                                )
                            )
                        )
                    )
                }
            }
        )
    }

    @CommandMethod("$arenasCommandPrefix info <mode> <arena>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun arenaInfo(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition
    ) = command(sender) {

        val teams = mapOf(*mode.modeTeams.map { it().let { it.name to it } }.toTypedArray())
        sender.audience.sendMessage(
            text { b ->
                val check = arena.checkValidity()
                val missingProperties = check.missingProperties
                val properties = arena.gameMode.properties.values.flatten()

                b.append(InfoComponent("Name", arena.name)).append(newline())
                b.append(InfoComponent("Id", arena.id)).append(newline())
                b.append(InfoComponent("World File Name", arena.worldFileName)).append(newline())
                b.append(InfoComponent("Is Valid", check.valid)).append(newline())
                b.append(InfoComponent("Teams", teams.values.joinToString { it.friendlyName }))

                if (properties.isNotEmpty()) {
                    b.append(newline())
                    b.append(text("Properties:", GREEN))
                    properties.forEach { prop ->
                        val isMissing = AtomicBoolean(missingProperties.contains(prop))

                        if (prop.type == GamePropertyType.TEAM) {
                            val teamPropPrefix = "team_${prop.name}_"
                            val missingForTeams =
                                check.missingPropertiesRawName.filter { it.startsWith(teamPropPrefix) }
                                    .map { it.removePrefix(teamPropPrefix) }.mapNotNull { teams[it] }

                            teams.values.forEach { team ->
                                isMissing.set(missingForTeams.any { it.name == team.name })
                                b.append(newline()).append(
                                    (text(" - " + prop.friendlyName, if (isMissing.get()) RED else GREEN))
                                        .append(text(" on ", GREEN)).append(
                                            team.displayComponent.colorIfAbsent(if (isMissing.get()) RED else GREEN)
                                        ).hoverEvent(text {
                                            it.append(text("Current value: ", GOLD))
                                            it.append(
                                                text(
                                                    arena[prop as GamePropertyDefinition<Any>, team]?.toString() ?: "<N/A>",
                                                    GOLD
                                                )
                                            )
                                        })
                                )
                            }
                            return@forEach
                        }

                        b.append(newline()).append(text(" - " + prop.friendlyName, if (isMissing.get()) RED else GREEN))
                    }
                }
            }
        )
    }

    private fun Component.createTeamPropertySetClickEvent(
        arena: ArenaDefinition,
        prop: GamePropertyDefinition<*>,
        team: GameTeam
    ): Component {
        val successfullySetValue = text {
            it.append(text("Successfully set ", GREEN))
            it.append(text(prop.friendlyName, GOLD))
            it.append(text(" for ", GREEN))
            it.append(team.displayComponent.colorIfAbsent(GOLD))
            it.append(text(" to ", GREEN))
        }
        return this.clickEvent {
            if (prop.javaType == ArenaLocation::class.java) {
                val value = ArenaLocation(location)
                arena.set(prop as GamePropertyDefinition<ArenaLocation>, value, team)
                sendMessage(successfullySetValue.append(text(value.toString(), GOLD)))
                ArenaManager.saveArena(arena)
            }
        }
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