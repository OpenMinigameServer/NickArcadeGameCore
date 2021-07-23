package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.kotlin.extension.commandBuilder
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.commands.ArenaDefinitionParser
import io.github.openminigameserver.gamecore.core.commands.GamePropertyParser
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.nickarcade.core.commandManager
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import net.kyori.adventure.text.Component
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
                        "mode"
                    ).withParser(ArenaDefinitionParser(mode))
                )

                registerCopy {
                    literal("set")
                    argument(
                        CommandArgument.ofType<ArcadeSender?, GamePropertyDefinition<*>>(
                            GamePropertyDefinition::class.java,
                            "name"
                        ).withParser(
                            GamePropertyParser(mode)
                        )
                    )

                    handler {
                        it.sender.audience.sendMessage(Component.text("bruh"))
                    }
                }

            }.register()
        }

    }

}