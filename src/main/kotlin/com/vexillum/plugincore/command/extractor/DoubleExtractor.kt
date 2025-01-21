package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.managers.language.LanguageAgent

open class DoubleExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> String
) : BaseArgumentExtractor<Sender, Double>() {

    override fun matchingScore(sender: Sender, value: String): Double =
        if (value.toDoubleOrNull() != null) 1.0 else -1.0

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value.toDouble()
    }

    override fun defaultDescriptor(user: CommandUser<*>): String =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): String =
        user.resolve(mapOf("value" to value)) { command.parsing.double }
}
