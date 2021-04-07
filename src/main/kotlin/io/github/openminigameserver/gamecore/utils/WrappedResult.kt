package io.github.openminigameserver.gamecore.utils

import com.fasterxml.jackson.annotation.JsonProperty

data class WrappedResultArray(
    @JsonProperty("result") val resultInternal: Array<Any>
) {
    inline fun <reified T> getResult(): List<T> = resultInternal.toList().map { it as T }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is WrappedResultArray) return false

        if (!resultInternal.contentEquals(other.resultInternal)) return false

        return true
    }

    override fun hashCode(): Int {
        return resultInternal.contentHashCode()
    }

}