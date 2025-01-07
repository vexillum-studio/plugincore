package com.vexillum.plugincore.managers.language

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.vexillum.plugincore.managers.language.node.LanguageArray
import com.vexillum.plugincore.managers.language.node.LanguageContainer
import com.vexillum.plugincore.managers.language.node.LanguageIdentity
import com.vexillum.plugincore.managers.language.node.LanguageObject
import com.vexillum.plugincore.managers.language.node.LanguageValue
import com.vexillum.plugincore.managers.language.node.ScopedNode
import com.vexillum.plugincore.util.JsonUtil.mapper as JsonMapper
import java.io.InputStream
import java.util.concurrent.atomic.AtomicInteger
import kotlin.reflect.KClass

class LanguageDeserializer<T : Any>(
    private val languageClass: KClass<T>
) {

    private val mapper =
        JsonMapper
            .copy()
            .registerModule(
                SimpleModule().apply {
                    addDeserializer(Message::class.java, TypeDeserializer())
                    addDeserializer(MessageList::class.java, TypeDeserializer())
                }
            )

    private val valueIndex = AtomicInteger()
    private val languageIdentities = mutableMapOf<Int, LanguageIdentity>()

    private fun deserialize(p: JsonParser): Message {
        val node = p.codec.readTree<JsonNode>(p)
        val id = node.get(ID_FIELD).asInt()
        val languageIdentity = languageIdentities.getValue(id)
        return languageIdentity.toNavigableMessage()
    }

    fun parseLanguage(languageStream: InputStream): T {
        languageStream.use {
            // First pass to traverse tree structure
            val languageNode = mapper.readTree(languageStream)
            val languageStructure = parseNode(languageNode)
            // Second pass to load messages
            return try {
                mapper.convertValue(languageStructure, languageClass.java)
            } catch (e: Exception) {
                val cause = e.cause
                if (cause is JsonMappingException) {
                    throw InvalidLanguageException(cause)
                }
                throw e
            }
        }
    }

    private fun parseNode(node: JsonNode): LanguageObject {
        val languageNode = node as? ObjectNode
            ?: error("The languages can only be defined with a json object")
        return parseNode(languageNode, null) as LanguageObject
    }

    private fun parseNode(node: JsonNode, parent: LanguageContainer?): ScopedNode =
        with(node) {
            when {
                node is ObjectNode -> {
                    val languageObject = LanguageObject(parent)
                    val fields = fields()
                    if (!fields.hasNext()) {
                        error("The empty language objects are not allowed")
                    }
                    for ((key, property) in fields) {
                        languageObject[key] = parseNode(property, languageObject)
                    }
                    languageObject
                }

                node is ArrayNode -> {
                    val languageArray = LanguageArray(parent!!, valueIndex.incrementAndGet())
                    for (innerValue in iterator()) {
                        val parsedValue = parseNode(innerValue, parent)
                        if (parsedValue !is LanguageValue) {
                            error("The language array can only contain simple text values")
                        }
                        languageArray.add(parsedValue)
                    }
                    languageArray
                }

                isValueNode || isTextual -> {
                    LanguageValue(parent!!, valueIndex.incrementAndGet(), asText())
                }

                else -> error("The language properties can only be defined as a string")
            }.also { createdNode ->
                if (createdNode is LanguageIdentity) {
                    languageIdentities[createdNode.id] = createdNode
                }
            }
        }

    private inner class TypeDeserializer<M : Message> : JsonDeserializer<M>() {

        @Suppress("UNCHECKED_CAST")
        override fun deserialize(parser: JsonParser, context: DeserializationContext?): M {
            val message = deserialize(parser)
            return message as? M ?: error("Unable to parse, expected Message or MessageList language node")
        }

    }

    companion object {
        private const val ID_FIELD = "id"
    }
}
