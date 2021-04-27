package io.github.openminigameserver.gamecore.core.team

import io.github.openminigameserver.gamecore.core.game.GameManager.teamSelectorAction
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.misc.RightClickSuffixComponent
import io.github.openminigameserver.nickarcade.core.ui.disableItalic
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GRAY
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class LobbyTeam : GameModeTeam("Lobby", GameMode.ADVENTURE, Material.AIR, Int.MAX_VALUE) {
    override fun onPlayerAdd(p: ArcadePlayer) {
        super.onPlayerAdd(p)
        val player = p.player
        if (player != null) {
            player.inventory.clear()
            if (game.hostingInfo.mode.isPrivate) {
                player.inventory.setItem(0, ItemStack(Material.NOTE_BLOCK).apply {
                    itemMeta = itemMeta.apply {
                        displayName(
                            RightClickSuffixComponent(Component.text("Team Selector", GREEN)).asComponent()
                                .disableItalic()
                        )
                        lore(
                            listOf(
                                Component.text("Click to select your team!", GRAY)
                                    .disableItalic()
                            )
                        )
                    }.teamSelectorAction()
                })
            }
        }
    }
}