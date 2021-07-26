//
// MIT License
//
// Copyright (c) 2021 Alexander Söderberg & Contributors
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.
//
package io.github.openminigameserver.gamecore.core.commands

import cloud.commandframework.arguments.CommandArgument
import cloud.commandframework.arguments.parser.ArgumentParseResult
import cloud.commandframework.arguments.parser.ArgumentParser
import cloud.commandframework.context.CommandContext
import cloud.commandframework.exceptions.parsing.NoInputProvidedException
import java.util.*

/**
 * [CommandArgument] type that recognizes fixed strings. This type does not parse variables.
 *
 * @param <C> Command sender type
</C> */
class DecentStaticArgument<C> private constructor(required: Boolean, name: String, vararg aliases: String) :
    CommandArgument<C, String>(required, name, DecentStaticArgumentParser(name, *aliases), String::class.java) {


    private class DecentStaticArgumentParser<C>(private val name: String, vararg aliases: String) :
        ArgumentParser<C, String> {
        private val allAcceptedAliases: MutableSet<String> = TreeSet(java.lang.String.CASE_INSENSITIVE_ORDER)
        override fun parse(
            commandContext: CommandContext<C>,
            inputQueue: Queue<String>
        ): ArgumentParseResult<String> {
            val string = inputQueue.peek()
                ?: return ArgumentParseResult.failure(
                    NoInputProvidedException(
                        DecentStaticArgumentParser::class.java,
                        commandContext
                    )
                )
            if (allAcceptedAliases.contains(string)) {
                inputQueue.remove()
                return ArgumentParseResult.success(string)
            }
            return ArgumentParseResult.failure(IllegalArgumentException(string))
        }

        override fun suggestions(
            commandContext: CommandContext<C>,
            input: String
        ): List<String> {
            return allAcceptedAliases.toList()
        }

        /**
         * Get the accepted strings
         *
         * @return Accepted strings
         */
        val acceptedStrings: Set<String>
            get() = allAcceptedAliases

        /**
         * Insert a new alias
         *
         * @param alias New alias
         */
        fun insertAlias(alias: String) {
            allAcceptedAliases.add(alias)
        }

        init {
            allAcceptedAliases.addAll(listOf(*aliases))
        }
    }

    companion object {
        /**
         * Create a new static argument instance for a required command argument
         *
         * @param name    Argument name
         * @param aliases Argument aliases
         * @param <C>     Command sender type
         * @return Constructed argument
        </C> */
        fun <C> of(
            name: String,
            vararg aliases: String
        ): DecentStaticArgument<C> {
            return DecentStaticArgument(true, name, *aliases)
        }
    }
}