package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import java.util.*

class GamePropertyParser<C>(val mode: GameModeDefinition? = null) : ArgumentParser<C, GamePropertyDefinition<*>> {
    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<GamePropertyDefinition<*>> {val mode = getContext(commandContext)

        val input = inputQueue.peek()
            ?: return ArgumentParseResult.failure(
                NoInputProvidedException(
                    GameParser::class.java,
                    commandContext
                )
            )

        val result = mode?.properties?.values?.flatten()?.first { it.name == input }
        if (result != null) {
            inputQueue.poll()
            return ArgumentParseResult.success(result)
        }

        return ArgumentParseResult.failure(Exception(input))
    }

    private fun getContext(ctx: CommandContext<C>) =
        mode ?: (ctx.asMap().values.firstOrNull { it is GameModeDefinition } as? GameModeDefinition)

    override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
        val mode = getContext(commandContext)
        mode ?: return super.suggestions(commandContext, input)

        return mode.properties.values.flatten().map { it.name }
    }
}