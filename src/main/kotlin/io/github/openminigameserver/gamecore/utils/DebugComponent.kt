package io.github.openminigameserver.gamecore.utils

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.ComponentLike
import net.kyori.adventure.text.format.NamedTextColor

class DebugComponent(val text: ComponentLike) : ComponentLike {
    override fun asComponent(): Component {
        return Component.text("Debug >", NamedTextColor.GOLD)
            .append(Component.space()).append(text.asComponent().colorIfAbsent(NamedTextColor.WHITE))
    }

}