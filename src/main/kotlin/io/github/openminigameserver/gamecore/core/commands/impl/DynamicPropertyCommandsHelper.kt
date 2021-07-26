package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ParserParameters
import cloud.commandframework.kotlin.extension.commandBuilder
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.arena.ArenaLocation
import io.github.openminigameserver.gamecore.core.arena.manager.ArenaManager
import io.github.openminigameserver.gamecore.core.commands.ArenaDefinitionParser
import io.github.openminigameserver.gamecore.core.commands.DecentStaticArgument
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyType
import io.github.openminigameserver.gamecore.core.team.selector.displayComponent
import io.github.openminigameserver.nickarcade.core.commandManager
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.github.openminigameserver.nickarcade.plugin.extensions.launchAsync
import io.leangen.geantyref.TypeToken
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.entity.Player
import java.util.*

object DynamicPropertyCommandsHelper {

    fun registerDynamicPropertyCommands(game: GameDefinition) {
        // Normal properties:
        // /<game> admin arenas <mode> <arena> properties set <name> <value>
        // /<game> admin arenas <mode> <arena> properties set <team> <name> <value>

        // List properties:
        // /<game> admin arenas properties <mode> <arena> add <name> <value>
        // /<game> admin arenas properties <mode> <arena> remove <name> <value>

        // Team List properties:
        // /<game> admin arenas properties <mode> <arena> add <team> <name> <value>
        // /<game> admin arenas properties <mode> <arena> remove <team> <name> <value>

        game.gameModes.values.forEach { mode ->
            commandManager.commandBuilder(game.name.lowercase(Locale.getDefault())) {
                this.literal("admin")
                this.literal("arenas")
                this.literal("properties")
                this.literal(mode.name.lowercase(Locale.getDefault()))
                this.argument(
                    CommandArgument.ofType<ArcadeSender, ArenaDefinition>(
                        ArenaDefinition::class.java,
                        "arena"
                    ).withParser(ArenaDefinitionParser(mode))
                )

                val modeTeams = mode.modeTeams.map { it() }
                val modeTeamsNames = modeTeams.map { it.name }

                mode.properties.values.flatten().distinctBy { it.name }.forEach { prop ->
                    registerCopy {
                        literal("set")
                        literal(prop.name)

                        if (prop.type == GamePropertyType.TEAM) {
                            argument(DecentStaticArgument.of("team", *modeTeamsNames.toTypedArray()))
                        }
                        if (prop.javaType != ArenaLocation::class.java) {
                            argument(
                                CommandArgument(
                                    true, "value",
                                    commandManager.parserRegistry.createParser(
                                        TypeToken.get(prop.javaType) as TypeToken<Any>, ParserParameters.empty(),
                                    ).orElseThrow(),
                                    prop.javaType as Class<Any>,
                                )
                            )
                        }

                        handler {
                            val arena = it.get<ArenaDefinition>("arena")
                            val team = it.getOptional<String>("team").orElse(null)
                                ?.let { modeTeams.first { t -> t.name.lowercase() == it.lowercase() } }

                            var value = it.getOptional<Any>("value").orElse(null)
                            if (prop.javaType == ArenaLocation::class.java && value == null) {
                                value = ArenaLocation((it.sender.commandSender as Player).location)
                            }

                            val successfullySetValue = Component.text { b ->
                                b.append(Component.text("Successfully set ", NamedTextColor.GREEN))
                                b.append(Component.text(prop.friendlyName, NamedTextColor.GOLD))
                                if (team != null) {
                                    b.append(Component.text(" for ", NamedTextColor.GREEN))
                                    b.append(team.displayComponent.colorIfAbsent(NamedTextColor.GOLD))
                                }
                                b.append(Component.text(" to ", NamedTextColor.GREEN))
                            }

                            if (value != null) {
                                val genericProp = prop as GamePropertyDefinition<Any>
                                if (team != null) {
                                    arena.set(genericProp, value, team)
                                } else {
                                    arena[genericProp] = value
                                }

                                launchAsync {
                                    ArenaManager.saveArena(arena)
                                }

                                it.sender.audience.sendMessage(
                                    successfullySetValue.append(
                                        Component.text(
                                            value.toString(),
                                            NamedTextColor.GOLD
                                        )
                                    )
                                )
                            }
                        }

                    }
                }

            }.register()

        }

    }
}