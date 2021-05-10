package io.github.openminigameserver.gamecore.core.game.properties.ui

import com.github.stefvanschie.inventoryframework.gui.type.ChestGui
import com.github.stefvanschie.inventoryframework.pane.OutlinePane
import com.github.stefvanschie.inventoryframework.pane.util.Mask
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.core.game.properties.GamePropertyDefinition
import io.github.openminigameserver.gamecore.core.game.properties.RequiredGamePropertyDefinition
import io.github.openminigameserver.gamecore.utils.InfoComponent
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.manager.getArcadeSender
import io.github.openminigameserver.nickarcade.core.ui.disableItalic
import io.github.openminigameserver.nickarcade.core.ui.guiItem
import io.github.openminigameserver.nickarcade.core.ui.itemMeta
import io.github.openminigameserver.nickarcade.plugin.extensions.launch
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Material
import org.bukkit.entity.HumanEntity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import kotlin.math.ceil

fun getContentRows(mode: GameModeDefinition): Int {
    return ceil(mode.properties.entries.sumBy { it.value.size } / 9.0).toInt()
}

class ArenaPropertySelector(
    val mode: GameModeDefinition,
    val arena: ArenaDefinition,
    val propertySelector: (GamePropertyDefinition<*>).(ArcadePlayer) -> Unit = {}
) : ChestGui(
    getContentRows(mode) + 2,
    "$mode Properties"
) {
    private val contentRows = getContentRows(mode)

    override fun show(humanEntity: HumanEntity) {
        panes.clear()
        addPane(OutlinePane(0, 1, 9, contentRows).apply {
            mode.properties.forEach { (type, props) ->
                props.forEach { prop: GamePropertyDefinition<*> ->
                    this.addItem(guiItem(ItemStack(Material.PAPER).itemMeta {
                        displayName(
                            Component.text("${type.name.toLowerCase().capitalize()} Property", NamedTextColor.GREEN)
                                .disableItalic()
                        )
                        lore(
                            listOf(
                                InfoComponent("Name", prop.friendlyName).asComponent()
                                    .disableItalic(),
                                InfoComponent("Required", prop is RequiredGamePropertyDefinition).asComponent()
                                    .disableItalic(),
                                InfoComponent("Value", (arena[prop as GamePropertyDefinition<Any>])).asComponent()
                                    .disableItalic(),
                            )
                        )
                    }) {
                        isCancelled = true
                        launch {
                            propertySelector(prop, (this@guiItem.whoClicked as Player).getArcadeSender())
                        }
                    })
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
                displayName(Component.text("NickArcade", NamedTextColor.YELLOW).disableItalic())
            }))
        })
        super.show(humanEntity)
    }

}