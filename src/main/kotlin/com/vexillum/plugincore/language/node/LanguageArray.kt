package com.vexillum.plugincore.language.node

import com.fasterxml.jackson.annotation.JsonValue
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageList

internal class LanguageArray(
    override val parent: LanguageContainer,
    override val id: Int,
    collection: MutableList<LanguageValue> = mutableListOf()
) : LanguageIdentity(), MutableCollection<LanguageValue> by collection {

    override val value get() = joinToString(separator = "\n")

    override fun toMessage(): Message =
        MessageList(map { it.toMessage() })

    override fun toString() = toList().toString()

    @JsonValue
    fun serialize() =
        LanguageValue(parent, id, value)
}
