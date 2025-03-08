package com.vexillum.plugincore.command.processor.validator

import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

open class NumberRangeValidator<Sender : LanguageAgent, T : Number>(
    private val min: T,
    private val max: T,
) : ArgumentValidator<Sender, T> {

    override fun defaultErrorMessage(user: CommandUser<*>, value: T): LanguageMessage =
        user.resolve(
            mapOf(
                "value" to value,
                "min" to min,
                "max" to max
            )
        ) { command.validation.numberRange }

    final override fun validate(user: CommandUser<Sender>, value: T) {
        if (value.toDouble() !in min.toDouble()..max.toDouble()) {
            throw CommandException(defaultErrorMessage(user, value))
        }
    }
}
