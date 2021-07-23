package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.arena.manager.ArenaManager
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import kotlinx.coroutines.runBlocking
import java.util.*

class ArenaDefinitionParser<C>(val mode: GameModeDefinition? = null) : ArgumentParser<C, ArenaDefinition> {

    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<ArenaDefinition> {
        val input = inputQueue.peek()
            ?: return ArgumentParseResult.failure(NoInputProvidedException(GameModeParser::class.java, commandContext))

        val mode = mode ?: commandContext.asMap().values.firstOrNull { it is GameModeDefinition } as? GameModeDefinition
        if (mode != null) {
            val result = runBlocking { ArenaManager.findArenaForGameModeDefinitionByName(mode, input) }
            if (result != null) {
                inputQueue.poll()
                return ArgumentParseResult.success(result)
            }
        }
        return ArgumentParseResult.failure(Exception(input))
    }

    override fun isContextFree(): Boolean {
        return false
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): List<String> {
        val mode = mode ?: commandContext.asMap().values.firstOrNull { it is GameModeDefinition } as? GameModeDefinition
        if (mode != null) {
            return runBlocking { ArenaManager.getArenaNamesForGameModeDefinition(mode) }
        }

        return super.suggestions(commandContext, input)
    }

}