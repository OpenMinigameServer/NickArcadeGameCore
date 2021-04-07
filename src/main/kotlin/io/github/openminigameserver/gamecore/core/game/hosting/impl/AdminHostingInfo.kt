package io.github.openminigameserver.gamecore.core.game.hosting.impl

import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingMode
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.core.manager.PlayerDataManager
import java.util.*

//Game is being hosted by an admin (Public Game converted to Private Game) and no one can join
class AdminHostingInfo(private val playerId: UUID, private val name: String) : GameHostingInfo() {
    constructor(player: ArcadePlayer) : this(player.uuid, player.actualDisplayName)

    suspend fun getPlayer(): ArcadePlayer = PlayerDataManager.getPlayerData(playerId, name)

    override val mode: GameHostingMode
        get() = GameHostingMode.PRIVATE_ADMIN

    override fun canJoin(player: ArcadePlayer): Boolean = false
}