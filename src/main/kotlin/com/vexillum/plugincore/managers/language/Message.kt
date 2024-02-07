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
