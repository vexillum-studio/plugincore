package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.extensions.trimEdges

internal sealed interface NavigableMessage {
    fun resolve(replacements: Map<String, Any>): String
}

class Message internal constructor(
    private val messages: Array<out NavigableMessage>
) : NavigableMessage {

    override fun resolve(replacements: Map<String, Any>): String =
        messages.joinToString(separator = "") {
            it.resolve(replacements)
        }
    }

    override fun toString() = raw

}

class MessageList(
    private val internalMessages: List<Message>
) : Message(internalMessages.toTypedArray()), List<Message> by internalMessages {

    override val raw = messages.joinToString(separator = "\n") {it.raw}

    override fun resolve(replacements: Map<String, Any>) =
        messages.joinToString(separator = "\n") {
            it.resolve(replacements)
        }

    override fun toString() = raw
}

internal data class MessageBlock(val block: String) : NavigableMessage {

    override fun resolve(replacements: Map<String, Any>) = block

    override fun toString() = block
}

internal data class ParameterBlock(private val enclosed: String) : NavigableMessage {

    private val parameter: String = enclosed.trimEdges()

    override fun resolve(replacements: Map<String, Any>): String =
        replacements[parameter]?.toString() ?: enclosed

    override fun toString(): String = enclosed
}
