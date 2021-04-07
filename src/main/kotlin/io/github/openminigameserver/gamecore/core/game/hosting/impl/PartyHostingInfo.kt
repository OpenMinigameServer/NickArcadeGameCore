package io.github.openminigameserver.gamecore.core.game.hosting.impl

import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingInfo
import io.github.openminigameserver.gamecore.core.game.hosting.GameHostingMode
import io.github.openminigameserver.nickarcade.core.data.sender.player.ArcadePlayer
import io.github.openminigameserver.nickarcade.party.model.MemberRole
import io.github.openminigameserver.nickarcade.party.model.Party
import io.github.openminigameserver.nickarcade.party.model.PartyManager
import java.util.*

//Game is being hosted by a party and only party members can join
data class PartyHostingInfo(val partyId: UUID) : GameHostingInfo() {
    private val party: Party?
        get() = PartyManager.getParty(partyId)

    override val mode: GameHostingMode
        get() = GameHostingMode.PRIVATE_PARTY

    override fun canJoin(player: ArcadePlayer): Boolean {
        val hostingParty = party
        return if (hostingParty != null) hostingParty.getPlayerRole(player) >= MemberRole.MEMBER else false
    }
}
