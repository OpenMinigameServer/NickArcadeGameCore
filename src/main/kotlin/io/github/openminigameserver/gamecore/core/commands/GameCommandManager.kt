package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.Command
import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.StaticArgument
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.keys.CloudKey
import cloud.commandframework.kotlin.extension.commandBuilder
import cloud.commandframework.meta.CommandMeta
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.commands.impl.ArenasCommands
import io.github.openminigameserver.gamecore.core.commands.impl.InfoCommands
import io.github.openminigameserver.gamecore.core.commands.impl.PropertyCommands
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.GameManager
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.nickarcade.core.commandAnnotationParser
import io.github.openminigameserver.nickarcade.core.commandManager
import io.github.openminigameserver.nickarcade.core.data.sender.ArcadeSender
import io.leangen.geantyref.TypeToken
import org.checkerframework.checker.nullness.qual.NonNull

object GameCommandManager {
    val gameCommandKey = CommandMeta.Key.of(GameDefinition::class.java, "game")

    init {
        commandManager.parserRegistry.registerParserSupplier(TypeToken.get(GameDefinition::class.java)) { GameParser() }
        commandManager.parserRegistry.registerParserSupplier(TypeToken.get(GameModeDefinition::class.java)) {
            GameModeParser()
        }
        commandManager.parserRegistry.registerParserSupplier(TypeToken.get(ArenaDefinition::class.java)) { ArenaDefinitionParser() }
    }

    private val gameCommands = listOf(
        commandAnnotationParser.parse(InfoCommands),
        commandAnnotationParser.parse(ArenasCommands),
        commandAnnotationParser.parse(PropertyCommands),
    )

    internal fun registerCommands() {
        createGameCommands(gameCommands.flatten())
    }

    private val registeredGameCommands = mutableListOf<String>()

    private val gameType: TypeToken<GameDefinition> = TypeToken.get(GameDefinition::class.java)
    private fun createGameCommands(gameCommands: List<Command<ArcadeSender>>) {
        GameManager.registeredGames.values.filterNot { registeredGameCommands.contains(it.name) }.forEach { game ->
            registeredGameCommands.add(game.name)
            val gameParser = GameModeParser<ArcadeSender>().apply { this.game = game }
            gameCommands.forEach { cmd ->
                val command = commandManager.commandBuilder(game.name.toLowerCase()) {
                    val arguments = cmd.arguments
                    // Drop "game" and "<game>"
                    arguments.drop(2).forEachIndexed { i, it ->
                        var argument =
                            (it as CommandArgument<ArcadeSender, Any>).copyWithParser((it.parser as ArgumentParser<ArcadeSender, Any>).let {
                                if (it is GameModeParser) gameParser else it
                            } as ArgumentParser<ArcadeSender, Any>)
                        if (argument.parser.javaClass.name.contains("StaticArgument")) {
                            argument = StaticArgument.of<ArcadeSender>(it.name) as CommandArgument<ArcadeSender, Any>
                        }
                        this.argument(argument)
                    }
                    this.meta(gameCommandKey, game)
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

    fun <C, T> CommandArgument<C, T>.copyWithParser(parser: ArgumentParser<C, T>): CommandArgument<C, T> {
        var builder: CommandArgument.Builder<C, T> = CommandArgument.ofType(this.valueType, this.name)
        builder = builder.withSuggestionsProvider(CustomDelegatingSuggestionsProvider(this.name, parser))
        builder = builder.withParser(parser)
        builder = when {
            this.isRequired -> {
                builder.asRequired()
            }
            this.defaultValue.isEmpty() -> {
                builder.asOptional()
            }
            else -> {
                builder.asOptionalWithDefault(this.defaultValue)
            }
        }
        builder = builder.withDefaultDescription(this.defaultDescription)
        return builder.build()
    }
}