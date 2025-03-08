package com.vexillum.plugincore.language.node

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import com.vexillum.plugincore.extensions.trimEdges
import com.vexillum.plugincore.language.Message
import com.vexillum.plugincore.language.MessageBlock
import com.vexillum.plugincore.language.ParameterBlock
import com.vexillum.plugincore.language.ReplacedBlock
import com.vexillum.plugincore.language.StartBlock
import java.util.regex.Pattern

internal abstract class LanguageIdentity : ScopedNode() {

    @get:JsonProperty(ID_FIELD)
    abstract val id: Int
    abstract val value: String

    @JsonIgnore
    private lateinit var resolved: Message

    private fun resolve(
        input: String,
        visitedScope: VisitedScope
    ): Message {
        var message: Message = StartBlock
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
            val resolvedProperty = scopeProperty(key, visitedScope)?.resolve(visitedScope)
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

    private fun resolve(visitedScope: VisitedScope = VisitedScope()): Message {
        if (!::resolved.isInitialized) {
            visitedScope.add(this)
            resolved = resolve(value, visitedScope)
        }
        return resolved
    }

    open fun toMessage(): Message =
        resolve()

    companion object {
        const val ID_FIELD = "id"
        private val REPLACEMENT_PATTERN = Pattern.compile("(\\{[a-zA-Z0-9._#-]+})")
    }
}
