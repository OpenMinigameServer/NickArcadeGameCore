package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import java.util.function.BiFunction

internal data class CustomDelegatingSuggestionsProvider<C>(
    private val argumentName: String,
    private val parser: ArgumentParser<C, *>
) : BiFunction<CommandContext<C>, String, List<String>> {
    override fun apply(context: CommandContext<C>, s: String): List<String> {
        return parser.suggestions(context, s)
    }
}