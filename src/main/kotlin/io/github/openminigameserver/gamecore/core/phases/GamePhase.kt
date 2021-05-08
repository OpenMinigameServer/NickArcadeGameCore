package io.github.openminigameserver.gamecore.core.phases

import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.gamecore.plugin.getEventListeners
import io.github.openminigameserver.gamecore.plugin.phaseBoundEvent
import kotlinx.coroutines.CoroutineScope
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import java.util.*

abstract class GamePhase(var name: String, var friendlyName: String) {
    val id: UUID = UUID.randomUUID()
    lateinit var game: GameInstance

    @PublishedApi
    internal val events: MutableList<GamePhaseEventData<out Event>> = mutableListOf()

    abstract suspend fun onStart()

    abstract suspend fun onEnd()

    open suspend fun onTick() {}

    /**
     * This method is called on a timer every second.
     */
    abstract suspend fun shouldEnd(): Boolean

    inline fun <reified T : Event> gameEvent(
        eventPriority: EventPriority = EventPriority.NORMAL,
        ignoreCancelled: Boolean = false,
        forceAsync: Boolean = false,
        forceBlocking: Boolean = false,
        noinline handler: suspend T.(CoroutineScope) -> Unit
    ) {
        events.add(
            GamePhaseEventData(
                T::class.java,
                handler as suspend Event.(CoroutineScope) -> Unit,
                eventPriority,
                ignoreCancelled,
                forceAsync,
                forceBlocking
            )
        )
    }

    internal fun registerEvents() {
        events.forEach { e ->
            e.registeredListener = phaseBoundEvent(
                this,
                e.eventType,
                e.priority,
                e.ignoreCancelled,
                e.forceAsync,
                e.forceBlocking,
                e.handler
            )
        }
    }

    internal fun unregisterEvents() {
        events.forEach { e ->
            e.registeredListener?.let { getEventListeners(e.eventType).unregister(it) }
        }
    }
}