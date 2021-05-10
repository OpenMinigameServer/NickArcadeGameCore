package io.github.openminigameserver.gamecore.core.team

import com.fasterxml.jackson.annotation.JsonIgnore
import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyType
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import org.bukkit.Material
import org.bukkit.scoreboard.Team
import java.util.*

abstract class GameTeam(
    var name: String,
    var friendlyName: String,
    var selectorMaterial: Material,
    var maxPlayers: Int
) {
    lateinit var game: GameInstance

    private val playerSet: MutableSet<ArcadePlayer> = mutableSetOf()
    val players: Set<ArcadePlayer> = Collections.unmodifiableSet(playerSet)

    fun addPlayer(p: ArcadePlayer) {
        if (playerSet.add(p)) {
            onPlayerAdd(p)
        }
    }

    fun removePlayer(p: ArcadePlayer) {
        if (playerSet.remove(p)) {
            onPlayerRemove(p)
        }
    }

    open fun configureScoreboardTeam(team: Team, target: ArcadePlayer, viewer: ArcadePlayer) {}

    open fun onPlayerAdd(p: ArcadePlayer) {}

    open fun onPlayerRemove(p: ArcadePlayer) {}

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GameTeam) return false

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }

    @JsonIgnore
    inline operator fun <reified T> get(prop: GamePropertyDefinition<T>): T? {
        if (prop.type != GamePropertyType.TEAM) throw Exception("Property ${prop.friendlyName} is not a property that can be used in teams.")
        return game.arena[prop, this]
    }

    @JsonIgnore
    inline operator fun <reified T> set(prop: GamePropertyDefinition<T>, value: T?) {
        game.arena.set(prop, value, this)
    }
}
