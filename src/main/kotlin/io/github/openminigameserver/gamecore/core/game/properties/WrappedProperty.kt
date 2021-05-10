package io.github.openminigameserver.gamecore.core.game.properties

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.JsonNode
import io.github.openminigameserver.hypixelapi.HypixelApi

class WrappedPropertyValue(
    value: Any?,
    val type: String
) {
    val valueNode: JsonNode = HypixelApi.objectMapper.valueToTree(value)

    constructor() : this(null, "")

    @get:JsonIgnore
    val value: Any
        get() {
            val clazzName = Class.forName(type)
            return HypixelApi.objectMapper.treeToValue(valueNode, clazzName)
        }

    override fun toString(): String {
        return value.toString()
    }
}
