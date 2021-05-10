package io.github.openminigameserver.gamecore.core.arena

import org.bukkit.Location
import org.bukkit.World
import java.math.MathContext

data class ArenaLocation(val x: Double, val y: Double, val z: Double, val yaw: Float, val pitch: Float) {
    constructor(location: Location) : this(location.x, location.y, location.z, location.yaw, location.pitch)

    fun toLocation(world: World): Location {
        return Location(world, x, y, z, yaw, pitch)
    }

    override fun toString(): String {
        val context = MathContext(2)
        return "x=${x.toBigDecimal().round(context)}, y=${y.toBigDecimal().round(context)}, z=${
            z.toBigDecimal().round(context)
        }"
    }
}