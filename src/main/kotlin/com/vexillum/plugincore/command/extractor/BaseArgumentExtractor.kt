package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.session.ConsoleUser
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.util.bestLevenshtein

abstract class BaseArgumentExtractor<Sender : LanguageAgent, Type : Any> : ArgumentExtractor<Sender, Type> {

    final override fun descriptor(user: CommandUser<*>): String =
        descriptor?.invoke(user) ?: defaultDescriptor(user)

    final override fun errorMessage(user: CommandUser<*>, value: String): String =
        errorMessage?.invoke(user, value) ?: defaultErrorMessage(user, value)

    override fun matchingScore(sender: Sender, value: String): Double =
        autocomplete(sender, value).map { it.value }.bestLevenshtein(value)?.let { (distance, bestMatch) ->
            val maxLength = bestMatch.length.coerceAtLeast(value.length)
            val matchScore = (maxLength - distance.toDouble()) / maxLength
            matchScore.coerceIn(0.0, 1.0)
        } ?: 0.0

    abstract fun defaultDescriptor(user: CommandUser<*>): String

    abstract fun defaultErrorMessage(user: CommandUser<*>, value: String): String

    override fun toString() =
        describe(ConsoleUser)
}
