package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

open class DoubleExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> LanguageMessage
) : BaseArgumentExtractor<Sender, Double>() {

    override fun matchingScore(sender: Sender, value: String): Double =
        if (value.toDoubleOrNull() != null) 1.0 else 0.0

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value.toDouble()
    }

    override fun defaultDescriptor(user: CommandUser<*>): LanguageMessage =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): LanguageMessage =
        user.resolve(mapOf("value" to value)) { command.parsing.double }
}
