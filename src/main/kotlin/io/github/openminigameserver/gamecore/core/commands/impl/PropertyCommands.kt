package io.github.openminigameserver.gamecore.core.commands.impl

import cloud.commandframework.annotations.Argument
import cloud.commandframework.annotations.CommandMethod
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.util.Mask
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.RequiredGamePropertyDefinition
import io.github.openminigameserver.gamecore.utils.InfoComponent
import io.github.openminigameserver.hypixelapi.models.HypixelPackageRank
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.ui.chestGui
import io.github.openminigameserver.nickarcade.core.ui.disableItalic
import io.github.openminigameserver.nickarcade.core.ui.guiItem
import io.github.openminigameserver.nickarcade.core.ui.itemMeta
import io.github.openminigameserver.nickarcade.plugin.helper.commands.RequiredRank
import net.kyori.adventure.text.Component.text
import net.kyori.adventure.text.format.NamedTextColor.GREEN
import net.kyori.adventure.text.format.NamedTextColor.YELLOW
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

object PropertyCommands {

    @CommandMethod("game <game> admin properties <mode> <arena>")
    @RequiredRank(HypixelPackageRank.ADMIN)
    fun gameListProperties(
        sender: ArcadePlayer,
        @Argument("game") game: GameDefinition,
        @Argument("mode") mode: GameModeDefinition,
        @Argument("arena") arena: ArenaDefinition,
    ) {
        val player = sender.player ?: return

        val count = mode.properties.entries.sumBy { it.value.size }
        val contentRows = ceil(count / 9.0).toInt()
        chestGui(contentRows + 2, text("$mode Properties")) {
            addPane(OutlinePane(0, 1, 9, contentRows).apply {
                mode.properties.forEach { (type, props) ->
                    props.forEach { prop: GamePropertyDefinition<*> ->
                        this.addItem(guiItem(ItemStack(Material.PAPER).itemMeta {
                            displayName(text("${type.name.toLowerCase().capitalize()} Property", GREEN).disableItalic())
                            lore(
                                listOf(
                                    InfoComponent("Name", prop.friendlyName).asComponent()
                                        .disableItalic(),
                                    InfoComponent("Required", prop is RequiredGamePropertyDefinition).asComponent()
                                        .disableItalic(),
                                    InfoComponent("Value", arena[prop as GamePropertyDefinition<Any>]).asComponent()
                                        .disableItalic(),
                                )
                            )
                        }))
                    }
                }
            })
            addPane(OutlinePane(9, contentRows + 2).apply {
                this.applyMask(
                    Mask(
                        "111111111",
                        *(0 until contentRows).map { "000000000" }.toTypedArray(),
                        "111111111",
                    )
                )
                setRepeat(true)
                addItem(guiItem(ItemStack(Material.RED_STAINED_GLASS_PANE).itemMeta {
                    displayName(text("NickArcade", YELLOW).disableItalic())
                }))
            })
        }.show(player)
    }
}