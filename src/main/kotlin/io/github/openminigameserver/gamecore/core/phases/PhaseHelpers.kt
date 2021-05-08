package io.github.openminigameserver.gamecore.core.phases

import org.bukkit.entity.EntityType
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent

fun GamePhase.disablePlayerDamage() {
    gameEvent<EntityDamageEvent>(forceBlocking = true) {
        if (this.entityType != EntityType.PLAYER) return@gameEvent
        isCancelled = this.cause != EntityDamageEvent.DamageCause.VOID
    }
}

fun GamePhase.disablePlayerHunger() {
    gameEvent<FoodLevelChangeEvent>(forceBlocking = true) {
        if (this.entityType != EntityType.PLAYER) return@gameEvent
        foodLevel = 20
        isCancelled = true
    }
}