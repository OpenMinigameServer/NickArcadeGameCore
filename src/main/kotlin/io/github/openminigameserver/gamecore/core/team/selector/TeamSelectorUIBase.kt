package io.github.openminigameserver.gamecore.core.team.selector

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.core.team.GameTeam
import io.github.openminigameserver.gamecore.core.team.LobbyTeam
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer

open class TeamSelectorUIBase(val game: GameInstance, val lobbyTeam: LobbyTeam) : ChestGui(1, "Team Selector") {
    val selectedTeamPlayers = mutableMapOf<String, MutableList<ArcadePlayer>>()

    protected fun getPlayerListForTeam(team: GameTeam): MutableList<ArcadePlayer> {
        return selectedTeamPlayers.getOrPut(team.name) { mutableListOf() }
    }

    protected fun setPlayerTeam(
        player: ArcadePlayer,
        team: GameTeam
    ) {
        if (getPlayerListForTeam(team).size < team.maxPlayers) {
            selectedTeamPlayers.forEach { teamEntry -> teamEntry.value.removeIf { it == player } }
            getPlayerListForTeam(team).add(player)
        }
    }

}