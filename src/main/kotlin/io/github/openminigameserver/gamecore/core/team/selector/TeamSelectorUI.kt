package io.github.openminigameserver.gamecore.core.team.selector

import com.github.stefvanschie.inventoryframework.gui.GuiItem
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.core.team.ColoredGameModeTeam
import io.github.openminigameserver.gamecore.core.team.GameTeam
import io.github.openminigameserver.gamecore.core.team.LobbyTeam
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.core.ui.disableItalic
import io.github.openminigameserver.nickarcade.core.ui.itemMeta
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.Component.empty
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.*
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.checkerframework.checker.nullness.qual.NonNull

val GameTeam.displayComponent
    get() = text(
        "$friendlyName Team",
        (this as? ColoredGameModeTeam)?.color ?: WHITE
    ).disableItalic()


class TeamSelectorUI(game: GameInstance, lobbyTeam: LobbyTeam) : TeamSelectorUIBase(game, lobbyTeam) {
    companion object {

        fun getTeamItemStack(team: GameTeam) =
            ItemStack(team.selectorMaterial).itemMeta {
                val displayNameComponent = team.displayComponent
                displayName(displayNameComponent)
            }
    }

    override fun show(humanEntity: HumanEntity) {
        panes.clear()
        addPane(OutlinePane(9, 1).apply {
            game.allTeams.filter { !it.selectorMaterial.isAir }.forEach { team ->
                addItem(getGameTeamItem(team))
            }
        })
        super.show(humanEntity)
    }

    fun getGameTeamItem(team: GameTeam) =
        GuiItem(getTeamItemStack(team).itemMeta {
            val players = getPlayerListForTeam(team)
            lore(getGameTeamItemLore(displayName() ?: empty(), players, team))
        }) { e ->
            e.isCancelled = true
            launch {
                val sender = (e.whoClicked as Player).getArcadeSender()

                setPlayerTeam(sender, team)
                this@TeamSelectorUI.update()
            }
        }


    private fun getGameTeamItemLore(
        displayNameComponent: @NonNull Component,
        players: MutableList<ArcadePlayer>,
        team: GameTeam
    ) = listOf(
        text {
            it.append(text("Click to join the ", GRAY))
            it.append(displayNameComponent)
            it.append(text(".", GRAY))
        }.disableItalic(), empty(),

        text("Current players:", GRAY).disableItalic(),
        *getCurrentPlayersComponent(players, team)
    )

    private fun getCurrentPlayersComponent(
        players: MutableList<ArcadePlayer>,
        team: GameTeam
    ): Array<out @NonNull Component> {
        val count = players.count()
        val max = team.maxPlayers.coerceAtLeast(players.count())

        val isFull = players.isNotEmpty() && count >= max
        return if (players.isEmpty()) {
            arrayOf(
                text("None", GRAY, TextDecoration.ITALIC), empty(),

                text("Click to join!", YELLOW).disableItalic()
            )
        } else {
            val playerCountColour = if (isFull) RED else YELLOW
            listOf(
                *players.map { text(it.displayName, GRAY).disableItalic() }.toTypedArray(),
                empty(),
                text {
                    it.append(text(count, playerCountColour))
                    it.append(text('/', playerCountColour))
                    it.append(
                        text(
                            if (team.maxPlayers == Int.MAX_VALUE) "∞" else team.maxPlayers.coerceAtLeast(players.count())
                                .toString(), playerCountColour
                        )
                    )
                }.disableItalic(),
                empty(),
                text(if (isFull) "That team is full!" else "Click to join!", playerCountColour).disableItalic()
            ).toTypedArray()
        }
    }

}