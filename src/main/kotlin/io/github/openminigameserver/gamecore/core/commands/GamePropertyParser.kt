package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import java.util.*

class GamePropertyParser<C>(val mode: GameModeDefinition? = null) : ArgumentParser<C, GamePropertyDefinition<*>> {
    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<GamePropertyDefinition<*>> {
        return ArgumentParseResult.failure(Throwable("E"))
    }

    private fun getContext(ctx: CommandContext<C>) =
        mode ?: (ctx.asMap().values.firstOrNull { it is GameModeDefinition } as? GameModeDefinition)

    override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
        val mode = getContext(commandContext)
        mode ?: return super.suggestions(commandContext, input)

        return mode.properties.values.flatten().map { it.name }
    }
}