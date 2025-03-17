package com.vexillum.plugincore.language.node

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vexillum.plugincore.extensions.trimEdges
import com.vexillum.plugincore.language.deserializer.MessageDeserializerContext
import com.vexillum.plugincore.language.message.EmptyBlock
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageBlock
import com.vexillum.plugincore.language.message.ParameterBlock
import com.vexillum.plugincore.language.message.ReplacedBlock
import java.util.regex.Pattern

internal abstract class LanguageResolver : LanguageNode() {

    abstract val value: String

    @JsonIgnore
    private lateinit var resolved: Message

    private fun resolve(
        context: MessageDeserializerContext,
        input: String,
        visitedScope: VisitedScope
    ): Message {
        visitedScope += this
        var message: Message = EmptyBlock
        var previousEndIndex = 0
        val matcher = REPLACEMENT_PATTERN.matcher(input)
        while (matcher.find()) {
            val result = matcher.toMatchResult()
            val startIndex = result.start()
            val endIndex = result.end()
            val matched = result.group()
            val key = matched.trimEdges()
            // Add text from the start or previous point to the replacement
            if (startIndex - previousEndIndex > 0) {
                message += MessageBlock(input.substring(previousEndIndex, startIndex))
            }
            val resolvedProperty = scopeResolver(context, key, visitedScope)?.resolve(context, visitedScope)
            if (resolvedProperty != null) {
                if (resolvedProperty.size == 1) {
                    message += ReplacedBlock(key, resolvedProperty.raw)
                } else {
                    message += resolvedProperty
                }
            } else {
                message += ParameterBlock(matched)
            }
            previousEndIndex = endIndex
        }
        // Add remaining text that hasn't been matched
        if (previousEndIndex <= input.lastIndex) {
            message += MessageBlock(input.substring(previousEndIndex))
        }
        return message
    }

    private fun resolve(
        context: MessageDeserializerContext,
        visitedScope: VisitedScope = VisitedScope()
    ): Message {
        if (!::resolved.isInitialized) {
            resolved = resolve(context, value, visitedScope)
        }
        return resolved
    }

    open fun toMessage(context: MessageDeserializerContext): Message =
        resolve(context)

    companion object {
        private val REPLACEMENT_PATTERN = Pattern.compile("(\\{[a-zA-Z0-9._#-]+})")
    }
}
