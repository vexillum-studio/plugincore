package com.vexillum.plugincore.managers.language

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.vexillum.plugincore.util.JsonUtil
import java.io.InputStream
import org.bukkit.ChatColor.translateAlternateColorCodes
import kotlin.reflect.KClass

class Language<out T : Any> private constructor(
    val localeLanguage: LocalLanguage,
    private val fields: Map<String, String>,
    internal val value: T
) {

    fun resolve(rawMessage: String, replacements: Map<String, Any>): String {
        val replacedMessage = REPLACEMENT_REGEX.replace(rawMessage) { matchResult ->
            val matchedValue = matchResult.value
            val key = with(matchedValue) { substring(1, length - 1) }
            fields[key]?.let { field -> resolve(field, replacements) }
                ?: replacements[key]?.toString()
                ?: matchedValue
        }
        return translateAlternateColorCodes(COLOR_CHAR, replacedMessage)
    }

    companion object {

        private const val COLOR_CHAR = '&'
        private val REPLACEMENT_REGEX = """(\{[\w]+})""".toRegex()

        fun <T : Any> create(
            localeLanguage: LocalLanguage,
            languageStream: InputStream,
            languageClass: KClass<T>
        ): Language<T> {
            val languageNode = JsonUtil.mapper.readTree(languageStream) as? ObjectNode
                ?: error("The language can only be defined with a json object")

            val fieldMap = mutableMapOf<String, String>()
            for ((key, property) in languageNode.fields()) {
                property.validate(key)?.let { rawMessage ->
                    fieldMap[key] = rawMessage
                }
            }

            val languageValue = JsonUtil.mapper.convertValue(languageNode, languageClass.java)

            return Language(localeLanguage, fieldMap, languageValue)
        }

        private fun JsonNode.validate(key: String): String? {
            if (this is ArrayNode) {
                try {
                    for (innerValue in iterator()) {
                        if (innerValue !is ValueNode) {
                            innerValue.validate(key)
                        }
                    }
                } catch (e: IllegalStateException) {
                    error("The language property '$key' can only contain string values")
                }
                return null
            }
            if (!isValueNode || !isTextual) {
                error("The language property '$key' can only be defined as a string")
            }
            return asText()
        }
    }
}

fun <T : Any> Language<T>.resolve(
    replacements: Map<String, Any> = emptyMap(),
    block: T.() -> Message
): String {
    val rawMessage = value.run(block).raw
    return resolve(rawMessage, replacements)
}
