package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.session.ConsoleUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.util.bestLevenshtein

abstract class BaseArgumentExtractor<Sender : LanguageAgent, Type : Any> : ArgumentExtractor<Sender, Type> {

    final override fun descriptor(user: CommandUser<*>): LanguageMessage =
        descriptor?.invoke(user) ?: defaultDescriptor(user)

    final override fun errorMessage(user: CommandUser<*>, value: String): LanguageMessage =
        errorMessage?.invoke(user, value) ?: defaultErrorMessage(user, value)

    override fun matchingScore(sender: Sender, value: String): Double =
        autocomplete(sender, value).map { it.value }.bestLevenshtein(value)?.let { (distance, bestMatch) ->
            val maxLength = bestMatch.length.coerceAtLeast(value.length)
            val matchScore = (maxLength - distance.toDouble()) / maxLength
            matchScore.coerceIn(0.5, 1.0)
        } ?: 0.5

    abstract fun defaultDescriptor(user: CommandUser<*>): LanguageMessage

    abstract fun defaultErrorMessage(user: CommandUser<*>, value: String): LanguageMessage

    override fun toString(): String =
        describe(ConsoleUser).resolved()
}
