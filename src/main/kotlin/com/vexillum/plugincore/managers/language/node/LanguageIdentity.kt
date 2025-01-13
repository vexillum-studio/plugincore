package com.vexillum.plugincore.managers.language.node

import com.fasterxml.jackson.annotation.JsonIgnore
import com.vexillum.plugincore.extensions.replaceInner
import com.vexillum.plugincore.extensions.takeWhen
import com.vexillum.plugincore.extensions.trimEdges
import com.vexillum.plugincore.managers.language.Message
import com.vexillum.plugincore.managers.language.MessageBlock
import com.vexillum.plugincore.managers.language.NavigableMessage
import com.vexillum.plugincore.managers.language.ParameterBlock
import org.bukkit.ChatColor
import java.util.regex.Pattern

internal abstract class LanguageIdentity : ScopedNode() {

    abstract val id: Int
    abstract val value: String

    @JsonIgnore
    private lateinit var resolved: String

    private val argumentRanges = mutableListOf<IntRange>()
    private val argumentRangesOut = mutableListOf<IntRange>()

    private fun addArgument(arg: IntRange, total: IntRange) {
        argumentRanges.add(arg)
        argumentRanges.removeIf { !total.contains(it.last) }
    }

    private fun removeArgument(arg: IntRange, total: IntRange) {
        argumentRangesOut.add(arg)
        argumentRangesOut.removeIf { !total.contains(it.last) }
        argumentRanges.removeIf { it == arg }
    }

    private fun IntRange.shift(shiftAmount: Int) =
        (start + shiftAmount)..(endInclusive + shiftAmount)

    private fun resolve(value: String, visitedScope: VisitedScope, depth: Int = 0): String? {
        var hasChanged: Boolean
        var replacedMessage = value
        do {
            var shifted = 0
            hasChanged = false
            replacedMessage = REPLACEMENT_REGEX.replace(replacedMessage) { matchResult ->
                val matched = matchResult.value
                val key = matched.trimEdges()

                val resolveExternalProperty = {
                    scopeProperty(key, visitedScope)?.resolve(visitedScope)
                        ?.also {
                            hasChanged = true
                        }
                }

                val resolveRecursively = {
                    resolve(key, visitedScope, depth + 1)
                        ?.let { matched.replaceInner(it) }
                        ?.also {
                            hasChanged = true
                        }
                }

                val replace = (resolveExternalProperty() ?: resolveRecursively())?.also { foundValue ->
                    if (depth == 0) {
                        val shiftedInReplacement = matched.length - foundValue.length
                        val startIndex = matchResult.range.first - shifted
                        removeArgument(foundValue.indices.shift(startIndex), replacedMessage.indices)
                        shifted += shiftedInReplacement
                    }
                } ?: matched.also {
                    addArgument(matchResult.range, replacedMessage.indices)
                }
                replace
            }
        } while (hasChanged)
        return takeWhen(replacedMessage != value) {
            replacedMessage
        }
    }

    private fun resolve(visitedScope: VisitedScope = VisitedScope()): String =
        if (::resolved.isInitialized) {
            resolved
        } else {
            visitedScope.add(this)
            val replacedMessage = resolve(value, visitedScope) ?: value
            ChatColor.translateAlternateColorCodes(COLOR_CHAR, replacedMessage).also { resolved = it }
        }

    open fun toNavigableMessage(): Message {
        val resolvedMessage = resolve()
        val blocks = if (argumentRanges.isEmpty()) {
            arrayOf(MessageBlock(resolvedMessage))
        } else {
            var currentArgument = 0
            var lastIndex = 0
            argumentRanges.sortedBy { it.first }
                .fold((mutableListOf<NavigableMessage>())) { list, argumentRange ->
                    if (argumentRange.first > lastIndex) {
                        list.add(MessageBlock(resolvedMessage.substring(lastIndex, argumentRange.first)))
                    }
                    lastIndex = argumentRange.last + 1
                    list.add(ParameterBlock(resolvedMessage.substring(argumentRange.first, lastIndex)))
                    if (
                        currentArgument == argumentRanges.lastIndex &&
                        lastIndex != resolvedMessage.length
                    ) {
                        list.add(MessageBlock(resolvedMessage.substring(lastIndex, resolvedMessage.length)))
                    }
                    currentArgument++
                    list
                }
                .toTypedArray()
        }
        return Message(blocks)
    }

    companion object {
        private const val COLOR_CHAR = '&'
        private val REPLACEMENT_REGEX = Pattern.compile("\\{([^{}]*|\\{[^{}]*})*}").toRegex()
    }
}
