package io.github.openminigameserver.gamecore.core.phases

import kotlin.time.Duration
import kotlin.time.seconds

abstract class TimedPhase(name: String, friendlyName: String, val duration: Duration) : GamePhase(name, friendlyName) {
    var elapsedTime: Duration = Duration.ZERO
        internal set
    val remainingTime: Duration get() = duration - elapsedTime

    protected open fun resetTimer() {
        elapsedTime = Duration.ZERO
    }

    override suspend fun onStart() {
        resetTimer()
    }

    /**
     * Called on whether or not we should reset the timer
     */
    open suspend fun shouldResetTimer(): Boolean = false

    override suspend fun shouldEnd(): Boolean {
        elapsedTime += 1.seconds
        if (shouldResetTimer()) {
            resetTimer()
            return hasPassedNow()
        }
        return hasPassedNow()
    }

    private fun hasPassedNow() = elapsedTime >= duration
}