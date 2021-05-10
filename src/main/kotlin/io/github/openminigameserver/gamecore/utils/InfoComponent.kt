package io.github.openminigameserver.gamecore.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor.*

class InfoComponent(private val info: String, private val value: Any?) : ComponentLike {
    override fun asComponent(): Component {
        return Component.text("$info: ", GREEN).append(
            Component.text(
                value?.toString() ?: "<N/A>",
                if (value is Boolean) {
                    if (value) DARK_GREEN else RED
                } else GOLD
            )
        )
    }
}