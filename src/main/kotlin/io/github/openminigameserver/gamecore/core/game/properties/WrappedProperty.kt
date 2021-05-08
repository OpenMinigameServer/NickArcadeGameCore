package io.github.openminigameserver.gamecore.core.game.properties

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import io.github.openminigameserver.hypixelapi.HypixelApi

class WrappedPropertyValue(
    value: Any
) {
    val valueNode: JsonNode = HypixelApi.objectMapper.valueToTree(value)
    val type: String = value.javaClass.name

    @get:JsonIgnore
    val value: Any
        get() = HypixelApi.objectMapper.treeToValue(valueNode, Class.forName(type))
}
