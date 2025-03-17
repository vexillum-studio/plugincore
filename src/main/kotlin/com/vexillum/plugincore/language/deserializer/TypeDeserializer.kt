@file:Suppress("UNCHECKED_CAST")

package com.vexillum.plugincore.language.deserializer

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.node.Address

internal class TypeDeserializer<M : Message>(
    private val context: MessageDeserializerContext
) : JsonDeserializer<M>() {

    override fun deserialize(parser: JsonParser, c: DeserializationContext?): M {
        val node = parser.codec.readTree<JsonNode>(parser)
        val address = Address.of(node.asText())
        val languageResolver = context.fromAddress(address)
            ?: throw IllegalArgumentException("The language '$address' is missing")
        val message = languageResolver.toMessage(context)
        return message as? M ?: error("Unable to parse, expected a Message or MessageList language node")
    }
}
