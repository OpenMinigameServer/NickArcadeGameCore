package io.github.openminigameserver.gamecore.core.team

import io.github.openminigameserver.gamecore.core.game.GameManager.teamSelectorAction
import io.github.openminigameserver.gamecore.core.players.currentTeam
import io.github.openminigameserver.gamecore.core.team.selector.TeamSelectorUI
import io.github.openminigameserver.gamecore.core.team.selector.TeamSelectorUIBase
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.misc.RightClickSuffixComponent
import io.github.openminigameserver.nickarcade.core.ui.disableItalic
import io.github.openminigameserver.nickarcade.party.model.getCurrentParty
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.*
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class LobbyTeam : GameModeTeam("lobby", "Lobby", GameMode.ADVENTURE, Material.AIR, Int.MAX_VALUE) {
    override fun onPlayerAdd(p: ArcadePlayer) {
        super.onPlayerAdd(p)
        val player = p.player
        if (player != null) {
            player.inventory.clear()
            if (game.hostingInfo.mode.isPrivate && (!game.isRolePlayGame || game.isHostPartyLeader(p))) {
                player.inventory.setItem(0, ItemStack(Material.NOTE_BLOCK).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            RightClickSuffixComponent(Component.text("Team Selector", GREEN)).asComponent()
                                .disableItalic()
                        )
                        lore(
                            listOf(
                                Component.text(
                                    if (game.isRolePlayGame) "Click to select player teams!" else "Click to select your team!",
                                    GRAY
                                ).disableItalic()
                            )
                        )
                    }.teamSelectorAction()
                })
            }
        }
    }

    private val teamSelectorUI: TeamSelectorUIBase by lazy {
        //TODO: Create Role Selector UI
        TeamSelectorUI(game, this)
    }

    fun openTeamSelectorMenu(player: ArcadePlayer) {
        player.player?.let { p ->
            teamSelectorUI.show(p)
        }
    }

    override fun onPlayerRemove(p: ArcadePlayer) {
        p.player?.inventory?.clear()
        super.onPlayerRemove(p)
    }

    fun applyTeamSelections() {
        val playersWithoutTeam = players.toMutableList()
        //Apply selected teams, if any
        teamSelectorUI.selectedTeamPlayers.forEach { (teamName, players) ->
            val team = game.allTeams.firstOrNull { it.name == teamName }
            if (team != null) players.forEach {
                it.currentTeam = team
                playersWithoutTeam.remove(it)
            }
        }

        val playersList = playersWithoutTeam.groupBy { it.getCurrentParty() }
        playersList.forEach { (party, players) ->
            val finalPlayersWithoutTeam = players.toMutableList()
            //If there is no party, pick a random team
            if (party == null) {
                if (players.isNotEmpty()) {
                    game.teams.forEach teamsForEach@{ team ->
                        val player = finalPlayersWithoutTeam.randomOrNull() ?: return@teamsForEach
                        finalPlayersWithoutTeam.remove(player)
                        player.currentTeam = team
                    }
                }
            } else {
                //If there is a party, check if party would fill all teams
                val willFillGameWithParty = party.totalMembersCount >= game.teams.sumOf { it.maxPlayers }
                if (willFillGameWithParty) {
                    //pick a random team for each member
                    party.membersList.shuffled().forEach { partyMember ->
                        partyMember.player.currentTeam = game.teams.firstOrNull { !it.isFull } ?: game.spectatorTeam
                    }
                } else {
                    //If the party will not fill the entire game,
                    // attempt to find a team that isn't full and that can fit the current party
                    val partyTargetTeam =
                        game.teams.filter { !it.isFull && it.maxPlayers >= it.players.count() + party.totalMembersCount }
                            .randomOrNull() ?: game.spectatorTeam
                    if (partyTargetTeam is SpectatorTeam) {
                        party.audience.sendMessage(
                            Component.text(
                                "We were unable to put your party in a team! You are now spectating the game.",
                                RED
                            )
                        )
                    }
                    players.forEach { it.currentTeam = partyTargetTeam }
                }
            }
        }

    }
}