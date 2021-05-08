package io.github.openminigameserver.gamecore.plugin

import io.github.openminigameserver.gamecore.core.phases.GamePhase
import io.github.openminigameserver.gamecore.core.players.GameInstanceManager
import io.github.openminigameserver.nickarcade.plugin.extensions.computeEventExecutor
import io.github.openminigameserver.nickarcade.plugin.extensions.pluginInstance
import kotlinx.coroutines.CoroutineScope
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.plugin.EventExecutor
import org.bukkit.plugin.IllegalPluginAccessException
import java.lang.reflect.Method


private fun getRegistrationClass(clazz: Class<out Event?>): Class<out Event?> {
    return try {
        clazz.getDeclaredMethod("getHandlerList")
        clazz
    } catch (e: NoSuchMethodException) {
        if (clazz.superclass != null && clazz.superclass != Event::class.java
            && Event::class.java.isAssignableFrom(clazz.superclass)
        ) {
            getRegistrationClass(clazz.superclass.asSubclass(Event::class.java))
        } else {
            throw IllegalPluginAccessException("Unable to find handler list for event " + clazz.name + ". Static getHandlerList method required!")
        }
    }
}

fun getEventListeners(type: Class<out Event?>): HandlerList {
    return try {
        val method: Method = getRegistrationClass(type).getDeclaredMethod("getHandlerList")
        method.isAccessible = true
        method.invoke(null) as HandlerList
    } catch (e: Exception) {
        throw IllegalPluginAccessException(e.toString())
    }
}


@Suppress("UNCHECKED_CAST")
internal fun phaseBoundEvent(
    phase: GamePhase,
    eventClazz: Class<out Event>,
    eventPriority: EventPriority = EventPriority.NORMAL, ignoreCancelled: Boolean = false, forceAsync: Boolean = false,
    forceBlocking: Boolean = false,
    code: suspend Event.(CoroutineScope) -> Unit
): Listener {
    val listener = object : Listener {}
    pluginInstance.server.pluginManager.registerEvent(
        eventClazz, listener, eventPriority,
        computePhaseEventExecutor(phase, forceAsync, forceBlocking, code),
        pluginInstance, ignoreCancelled,
        phase.game.worldArena
    )

    return listener
}

private fun computePhaseEventExecutor(
    phase: GamePhase,
    forceAsync: Boolean,
    forceBlocking: Boolean,
    code: suspend Event.(CoroutineScope) -> Unit
): EventExecutor {
    val gameId = phase.game.id
    val phaseId = phase.id
    val original = computeEventExecutor(forceAsync, forceBlocking, code)
    return EventExecutor { listener: Listener, event: Event ->
        val actualGame = GameInstanceManager.getGameById(gameId) ?: return@EventExecutor
        if (actualGame.currentPhase.id != phaseId) return@EventExecutor
        original.execute(listener, event)
    }
}