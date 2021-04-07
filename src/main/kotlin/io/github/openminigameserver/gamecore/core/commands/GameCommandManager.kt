package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.Command
import cloud.commandframework.arguments.StaticArgument
import cloud.commandframework.keys.CloudKey
import cloud.commandframework.kotlin.extension.commandBuilder
import io.github.openminigameserver.gamecore.core.commands.impl.InfoCommands
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.GameManager
import io.github.openminigameserver.gamecore.core.game.mode.MiniGameMode
import io.github.openminigameserver.nickarcade.core.commandAnnotationParser
import io.github.openminigameserver.nickarcade.core.commandManager
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.leangen.geantyref.TypeToken
import org.checkerframework.checker.nullness.qual.NonNull

object GameCommandManager {
    fun registerCommands() {
        commandManager.parserRegistry.registerParserSupplier(TypeToken.get(GameDefinition::class.java)) { GameParser() }
        commandManager.parserRegistry.registerParserSupplier(TypeToken.get(MiniGameMode::class.java)) { GameModeParser() }

        val gameCommands = listOf(
            commandAnnotationParser.parse(InfoCommands)
        )

        createGameCommands(gameCommands.flatten())
    }

    private val gameType: TypeToken<GameDefinition> = TypeToken.get(GameDefinition::class.java)
    private fun createGameCommands(gameCommands: List<Command<ArcadeSender>>) {
        GameManager.registeredGames.values.forEach { game ->
            gameCommands.forEach { cmd ->
                commandManager.commandBuilder(game.name.toLowerCase()) {
                    val arguments = cmd.arguments
                    // Drop "game" and "<game>"
                    arguments.drop(2).forEachIndexed { i, it ->
                        var argument = it.copy()
                        if (argument.parser.javaClass.name.contains("StaticArgument")) {
                            argument = StaticArgument.of(it.name)
                        }
                        argument.parser.let { it as? GameModeParser<ArcadeSender> }?.let {
                            it.game = game
                        }
                        this.argument(argument)
                    }
                    this.permission(cmd.commandPermission)
                    cmd.senderType.orElse(null)?.let { this.senderType(it) }
                    this.hidden(cmd.isHidden)
                    this.handler {
                        it.set(
                            arguments.first { arg -> arg.valueType == gameType }.key as @NonNull CloudKey<GameDefinition>,
                            game
                        )
                        cmd.commandExecutionHandler.execute(it)
                    }
                }.register()
            }
        }
    }
}