package io.github.openminigameserver.gamecore.core.game

import net.kyori.adventure.audience.Audience
import net.kyori.adventure.audience.ForwardingAudience

class GameAudience(val game: GameInstance) : ForwardingAudience {
    override fun audiences(): Iterable<Audience> {
        return game.teams.flatMap { it.players }.map { it.audience }
    }
}