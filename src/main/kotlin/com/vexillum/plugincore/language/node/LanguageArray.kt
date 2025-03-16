package com.vexillum.plugincore.language.node

import com.vexillum.plugincore.language.LanguageDeserializer.LanguageDeserializerContext
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageList

internal class LanguageArray(
    override val address: Address,
    collection: MutableList<LanguageValue> = mutableListOf()
) : LanguageResolver(), MutableCollection<LanguageValue> by collection {

    override val value get() = joinToString(separator = "\n")

    override fun toMessage(context: LanguageDeserializerContext): Message =
        MessageList(map { it.toMessage(context) })

    override fun toString() = toList().toString()
}
