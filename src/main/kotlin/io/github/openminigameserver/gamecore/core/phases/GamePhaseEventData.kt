package io.github.openminigameserver.gamecore.core.phases

import kotlinx.coroutines.CoroutineScope
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener

data class GamePhaseEventData<T : Event>(
    val eventType: Class<T>,
    val handler: suspend Event.(CoroutineScope) -> Unit,
    val priority: EventPriority = EventPriority.NORMAL,
    val ignoreCancelled: Boolean = false,
    val forceAsync: Boolean = false,
    val forceBlocking: Boolean = false,
    var registeredListener: Listener? = null
)
