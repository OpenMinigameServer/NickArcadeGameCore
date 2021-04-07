package io.github.openminigameserver.gamecore.core.arena.manager

import com.mongodb.client.model.Accumulators
import io.github.openminigameserver.gamecore.core.arena.ArenaDefinition
import io.github.openminigameserver.gamecore.core.arena.manager.io.SerializedMiniGameMode
import io.github.openminigameserver.gamecore.core.game.GameDefinition
import io.github.openminigameserver.gamecore.core.game.mode.GameModeDefinition
import io.github.openminigameserver.gamecore.utils.WrappedResultArray
import io.github.openminigameserver.nickarcade.core.database
import io.github.openminigameserver.nickarcade.core.div
import org.litote.kmongo.*

object ArenaManager {

    private val arenasCollection by lazy {
        database.getCollection<ArenaDefinition>("arenas")
    }

    suspend fun saveArena(arena: ArenaDefinition) {
        arenasCollection.updateOneById(arena.id, arena, upsert())
    }

    suspend fun removeArena(arena: ArenaDefinition): Boolean {
        return arenasCollection.deleteOneById(arena.id).deletedCount > 0
    }

    suspend fun getArenasForGame(game: GameDefinition): List<ArenaDefinition> {
        return arenasCollection.find(ArenaDefinition::gameMode / SerializedMiniGameMode::gameName eq game.name).toList()
    }

    suspend fun getArenasForGameModeDefinition(mode: GameModeDefinition): List<ArenaDefinition> {
        return arenasCollection.find(
            and(
                ArenaDefinition::gameMode / SerializedMiniGameMode::gameName eq mode.game.name,
                ArenaDefinition::gameMode / SerializedMiniGameMode::modeName eq mode.name,
            )
        ).toList()
    }

    suspend fun getArenaNamesForGameModeDefinition(mode: GameModeDefinition): List<String> {
        val resultNode = arenasCollection.aggregate<WrappedResultArray>(
            listOf(
                match(
                    and(
                        ArenaDefinition::gameMode / SerializedMiniGameMode::gameName eq mode.game.name,
                        ArenaDefinition::gameMode / SerializedMiniGameMode::modeName eq mode.name,
                    )
                ),
                group(null, Accumulators.push("result", "\$name"))
            )
        )
        return resultNode.first()?.getResult() ?: emptyList()
    }

    suspend fun findArenaForGameModeDefinitionByName(mode: GameModeDefinition, name: String): ArenaDefinition? {
        return arenasCollection.findOne(
            and(
                ArenaDefinition::gameMode / SerializedMiniGameMode::gameName eq mode.game.name,
                ArenaDefinition::gameMode / SerializedMiniGameMode::modeName eq mode.name,
                ArenaDefinition::name eq name
            )
        )
    }

}