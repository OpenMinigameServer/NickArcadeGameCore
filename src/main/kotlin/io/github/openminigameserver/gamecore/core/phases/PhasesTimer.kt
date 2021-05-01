package io.github.openminigameserver.gamecore.core.phases

import io.github.openminigameserver.gamecore.core.game.GameInstance
import io.github.openminigameserver.nickarcade.plugin.extensions.launchAsync
import io.github.openminigameserver.nickarcade.plugin.extensions.sync
import kotlinx.coroutines.delay
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor.GOLD
import kotlin.time.seconds

class PhasesTimer(val game: GameInstance) {
    var skipCurrent: Boolean = false

    var isRunning = false

    fun launch() {
        isRunning = true
        launchAsync {
            game.currentPhase.onStart()
            while (isRunning) {
                if (game.phases.isEmpty()) {
                    isRunning = false
                    return@launchAsync
                }

                val currentPhase = game.currentPhase
                val debugColor = GOLD
                if (game.isDeveloperGame && currentPhase is TimedPhase) {
                    game.audience.sendActionBar(
                        Component.text(
                            "[DEBUG] Ending Timed phase ${currentPhase.javaClass.simpleName} in ${(currentPhase.remainingTime)}",
                            debugColor
                        )
                    )
                }
                sync { game.currentPhase.onTick() }
                if (skipCurrent || game.currentPhase.shouldEnd()) {
                    if (game.isDeveloperGame) {
                        game.audience.sendActionBar(
                            Component.text(
                                "[DEBUG] Ending current phase ${currentPhase.javaClass.simpleName}",
                                debugColor
                            )
                        )
                    }
                    sync { game.currentPhase.onEnd() }
                    game.phases.removeFirstOrNull()

                    if (game.isDeveloperGame) {
                        game.audience.sendActionBar(
                            Component.text(
                                "[DEBUG] Starting new phase ${game.currentPhase.javaClass.simpleName}",
                                debugColor
                            )
                        )
                    }
                    sync { game.currentPhase.onStart() }
                    skipCurrent = false
                }
                delay(1.seconds)
            }
        }
    }

    fun stop() {
        isRunning = false
    }
}