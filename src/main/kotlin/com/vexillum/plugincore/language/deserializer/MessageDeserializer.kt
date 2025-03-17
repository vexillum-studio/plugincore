package com.vexillum.plugincore.language.deserializer

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.vexillum.plugincore.extensions.tryCastOrNull
import com.vexillum.plugincore.language.InvalidLanguageException
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.MissingLanguageKeyException
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageList
import com.vexillum.plugincore.language.message.MessageReplacements
import com.vexillum.plugincore.language.message.messageReplacements
import com.vexillum.plugincore.language.node.Address
import com.vexillum.plugincore.language.node.Address.Root
import com.vexillum.plugincore.language.node.LanguageArray
import com.vexillum.plugincore.language.node.LanguageNode
import com.vexillum.plugincore.language.node.LanguageObject
import com.vexillum.plugincore.language.node.LanguageResolver
import com.vexillum.plugincore.language.node.LanguageValue
import java.io.InputStream
import kotlin.reflect.KClass
import com.vexillum.plugincore.util.JsonUtil.mapper as jsonUtilMapper

private fun <R> useMessageDeserializer(block: (MessageDeserializerContext) -> R): R {
    val context = MessageDeserializerContext()
    return block(context)
}

internal class MessageDeserializerContext {

    val mapper: ObjectMapper = jsonUtilMapper
        .copy()
        .registerModule(
            SimpleModule().apply {
                addDeserializer(
                    Message::class.java,
                    TypeDeserializer(this@MessageDeserializerContext)
                )
                addDeserializer(
                    MessageList::class.java,
                    TypeDeserializer(this@MessageDeserializerContext)
                )
            }
        )

    private val languageNodes = mutableMapOf<Address, LanguageNode>()

    fun addNode(scopedNode: LanguageNode) {
        languageNodes[scopedNode.address] = scopedNode
    }

    fun fromAddress(address: Address): LanguageResolver? =
        languageNodes[address] as? LanguageResolver

    fun extractReplacements(): MessageReplacements {
        val replacements = messageReplacements()
        for ((address, node) in languageNodes) {
            if (node is LanguageResolver) {
                replacements.replace(address.toString(), node.toMessage(this))
            }
        }
        return replacements
    }

    fun <T : Language> convert(
        languageNode: LanguageNode,
        clazz: KClass<T>
    ): T =
        try {
            val objectNode = languageNode as? LanguageObject
                ?: error("The languages can only be defined with a json object")
            mapper.convertValue(objectNode, clazz.java).tryCastOrNull<T>()
                ?: error("Unable to deserialize into: ${clazz.simpleName}")
        } catch (e: Exception) {
            val cause = e.cause
            if (cause is MismatchedInputException) {
                throw MissingLanguageKeyException(cause)
            }
            throw InvalidLanguageException(e)
        }
}

class DeserializerResult<T : Language>(
    val language: T,
    val replacements: MessageReplacements
)

internal object MessageDeserializer {

    fun deserialize(messageString: String): Message =
        useMessageDeserializer { context ->
            val jsonNode = context.mapper.valueToTree<JsonNode>(messageString)
            val languageNode = deserialize(jsonNode, context) as? LanguageResolver
                ?: error("Unable to deserialize a single message value from string: $messageString")
            languageNode.toMessage(context)
        }

    fun <T : Language> deserialize(
        languageStream: InputStream,
        clazz: KClass<T>
    ): DeserializerResult<T> =
        useMessageDeserializer { context ->
            languageStream.use {
                val languageNode = deserialize(
                    context.mapper.readTree(languageStream),
                    context
                )
                val language = context.convert(languageNode, clazz)
                val replacements = context.extractReplacements()
                DeserializerResult(language, replacements)
            }
        }

    fun deserialize(
        node: JsonNode,
        context: MessageDeserializerContext,
        nodeAddress: Address = Root
    ): LanguageNode =
        with(node) {
            val createdNode = when {
                node is ObjectNode -> {
                    val languageObject = LanguageObject(address = nodeAddress)
                    val fields = fields()
                    if (!fields.hasNext()) {
                        error("The empty language objects are not allowed")
                    }
                    for ((propertyKey, property) in fields) {
                        languageObject[propertyKey] = deserialize(
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
                        val parsedValue = deserialize(
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
            }
            context.addNode(createdNode)
            createdNode
        }
}
