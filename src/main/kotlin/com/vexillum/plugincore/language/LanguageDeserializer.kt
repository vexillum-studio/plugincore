package com.vexillum.plugincore.language

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageList
import com.vexillum.plugincore.language.node.Address
import com.vexillum.plugincore.language.node.Address.Root
import com.vexillum.plugincore.language.node.LanguageArray
import com.vexillum.plugincore.language.node.LanguageNode
import com.vexillum.plugincore.language.node.LanguageObject
import com.vexillum.plugincore.language.node.LanguageResolver
import com.vexillum.plugincore.language.node.LanguageValue
import java.io.InputStream
import kotlin.reflect.KClass
import com.vexillum.plugincore.util.JsonUtil.mapper as JsonMapper

class LanguageDeserializer<T : Any>(
    private val languageClass: KClass<T>
) {

    fun parseLanguage(languageStream: InputStream): T {
        val context = LanguageDeserializerContext()
        val mapper =
            JsonMapper
                .copy()
                .registerModule(
                    SimpleModule().apply {
                        addDeserializer(Message::class.java, TypeDeserializer(context))
                        addDeserializer(MessageList::class.java, TypeDeserializer(context))
                    }
                )
        languageStream.use {
            // First traverse tree structure
            val rootLanguageObject = parseNode(mapper.readTree(languageStream), context)
            // Second pass to load messages
            return try {
                mapper.convertValue(rootLanguageObject, languageClass.java)
            } catch (e: Exception) {
                val cause = e.cause
                if (cause is MismatchedInputException) {
                    throw MissingLanguageKeyException(cause)
                }
                throw InvalidLanguageException(e)
            }
        }
    }

    private fun parseNode(node: JsonNode, context: LanguageDeserializerContext): LanguageObject {
        val languageNode = node as? ObjectNode
            ?: error("The languages can only be defined with a json object")
        return parseNode(context, languageNode) as LanguageObject
    }

    private fun parseNode(
        context: LanguageDeserializerContext,
        node: JsonNode,
        nodeAddress: Address = Root
    ): LanguageNode =
        with(node) {
            when {
                node is ObjectNode -> {
                    val languageObject = LanguageObject(address = nodeAddress)
                    val fields = fields()
                    if (!fields.hasNext()) {
                        error("The empty language objects are not allowed")
                    }
                    for ((propertyKey, property) in fields) {
                        languageObject[propertyKey] = parseNode(
                            context = context,
                            node = property,
                            nodeAddress = nodeAddress.child(propertyKey)
                        )
                    }
                    languageObject
                }

                node is ArrayNode -> {
                    val languageArray = LanguageArray(nodeAddress)
                    for (innerValue in iterator()) {
                        val parsedValue = parseNode(
                            context = context,
                            node = innerValue,
                            nodeAddress = nodeAddress
                        )
                        if (parsedValue !is LanguageValue) {
                            error("The language array can only contain simple text values")
                        }
                        languageArray.add(parsedValue)
                    }
                    languageArray
                }

                isValueNode || isTextual -> {
                    LanguageValue(
                        address = nodeAddress,
                        value = asText()
                    )
                }

                else -> error("The language properties can only be textually defined")
            }.also { createdNode ->
                context.addNode(createdNode)
            }
        }

    internal class LanguageDeserializerContext {

        private val languageNodes = mutableMapOf<Address, LanguageNode>()

        fun addNode(scopedNode: LanguageNode) {
            languageNodes[scopedNode.address] = scopedNode
        }

        fun fromAddress(address: Address): LanguageResolver? =
            languageNodes[address] as? LanguageResolver
    }

    private inner class TypeDeserializer<M : Message>(
        private val context: LanguageDeserializerContext
    ) : JsonDeserializer<M>() {

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(parser: JsonParser, c: DeserializationContext?): M {
            val node = parser.codec.readTree<JsonNode>(parser)
            val address = Address.of(node.asText())
            val languageResolver = context.fromAddress(address)
                ?: throw IllegalArgumentException("The language '$address' is missing")
            val message = languageResolver.toMessage(context)
            return message as? M ?: error("Unable to parse, expected a Message or MessageList language node")
        }
    }
}
