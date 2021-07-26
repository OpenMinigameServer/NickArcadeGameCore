package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.bukkit.BukkitCommandContextKeys
import cloud.commandframework.bukkit.parsers.location.LocationArgument
import cloud.commandframework.context.CommandContext
import io.github.openminigameserver.gamecore.core.arena.ArenaLocation
import org.bukkit.Location
import org.bukkit.command.BlockCommandSender
import org.bukkit.entity.Entity
import java.util.*

class ArenaLocationParser<C> : ArgumentParser<C, ArenaLocation> {
    private val locationParser = LocationArgument.LocationParser<C>()

    override fun parse(
        commandContext: CommandContext<C>,
        inputQueue: Queue<String>
    ): ArgumentParseResult<ArenaLocation> {
        if (inputQueue.isEmpty()) {
            val bukkitSender = commandContext.get(BukkitCommandContextKeys.BUKKIT_COMMAND_SENDER)
            var originalLocation: Location? = null

            if (bukkitSender is BlockCommandSender) {
                originalLocation = bukkitSender.block.location
            } else if (bukkitSender is Entity) {
                originalLocation = bukkitSender.location
            }

            if (originalLocation != null) return ArgumentParseResult.success(ArenaLocation(originalLocation))

        }
        val result = locationParser.parse(commandContext, inputQueue)
        val parsedResult = result.parsedValue.orElse(null)

        if (parsedResult != null) {
            return ArgumentParseResult.success(ArenaLocation(parsedResult))
        }

        return ArgumentParseResult.failure(result.failure.get())
    }

    override fun getRequestedArgumentCount(): Int {
        return locationParser.requestedArgumentCount
    }

    override fun suggestions(commandContext: CommandContext<C>, input: String): MutableList<String> {
        return locationParser.suggestions(commandContext, input)
    }
}