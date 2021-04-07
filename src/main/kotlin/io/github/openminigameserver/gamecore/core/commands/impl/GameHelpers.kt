package io.github.openminigameserver.gamecore.core.commands.impl

import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.core.players.currentGame
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.separator
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor

fun gameCommand(sender: ArcadePlayer, game: GameDefinition, code: (GameInstance) -> Unit) {
    val currentGame = sender.currentGame ?: run {
        sender.audience.sendMessage(separator {
            append(Component.text("You are not in a game.", NamedTextColor.RED))
        })
        return
    }
    if (currentGame.game.name != game.name) {
        sender.audience.sendMessage(separator {
            append(Component.text("You are not in a game of ${game.friendlyName}.", NamedTextColor.RED))
        })
        return
    }

    code(currentGame)
}