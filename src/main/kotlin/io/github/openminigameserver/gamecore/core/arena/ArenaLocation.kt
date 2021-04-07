package io.github.openminigameserver.gamecore.core.arena

import org.bukkit.Location
import org.bukkit.World

data class ArenaLocation(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) {
    constructor(location: Location) : this(location.x, location.y, location.z, location.yaw, location.pitch)

    fun toLocation(world: World): Location {
        return Location(world, x, y, z, yaw, pitch)
    }
}