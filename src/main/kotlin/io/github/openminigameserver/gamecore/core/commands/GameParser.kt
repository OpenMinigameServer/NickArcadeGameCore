package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.GameManager
import java.util.*

class GameParser<C> : ArgumentParser<C, GameDefinition> {
    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<GameDefinition> {

        val input = inputQueue.peek()
            ?: return ArgumentParseResult.failure(
                NoInputProvidedException(
                    GameParser::class.java,
                    commandContext
                )
            )

        val result = GameManager.registeredGames[input.toUpperCase()]

        if (result != null) {
            inputQueue.poll()
            return ArgumentParseResult.success(result)
        }

        return ArgumentParseResult.failure(Exception(input))
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
        return GameManager.registeredGames.keys.map { it.toLowerCase() }
    }
}