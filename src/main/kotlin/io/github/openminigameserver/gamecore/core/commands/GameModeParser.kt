package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import io.github.openminigameserver.gamecore.core.commands.GameCommandManager.gameCommandKey
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import java.util.*

class GameModeParser<C> : ArgumentParser<C, GameModeDefinition> {
    var game: GameDefinition? = null

    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<GameModeDefinition> {
        val input = inputQueue.peek()
            ?: return ArgumentParseResult.failure(NoInputProvidedException(GameModeParser::class.java, commandContext))

        val game = this.game ?: commandContext.asMap().values.firstOrNull { it is GameDefinition } as? GameDefinition
        if (game != null) {
            val entry = game.gameModes[input.toUpperCase()]
            if (entry != null) {
                inputQueue.poll()
                return ArgumentParseResult.success(entry)
            }
        }
        return ArgumentParseResult.failure(Exception(input))
    }

    override fun isContextFree(): Boolean {
        return false
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
        val game = this.game ?: commandContext.currentArgument?.owningCommand?.commandMeta?.get(gameCommandKey)?.orElse(null) ?: commandContext.asMap().values.firstOrNull { it is GameDefinition } as? GameDefinition
        if (game != null) {
            return game.gameModes.keys.map { it.toLowerCase() }
        }
        return super.suggestions(commandContext, input)
    }
}