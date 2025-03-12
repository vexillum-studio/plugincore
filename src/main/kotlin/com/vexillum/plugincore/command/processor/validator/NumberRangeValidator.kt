package com.vexillum.plugincore.command.processor.validator

import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message

open class NumberRangeValidator<Sender : LanguageAgent, T : Number>(
    private val min: T,
    private val max: T,
) : ArgumentValidator<Sender, T> {

    override fun defaultErrorMessage(user: CommandUser<*>, value: T): Message =
        user.resolve { command.validation.numberRange }.replacing(
            "value" to value,
            "min" to min,
            "max" to max
        )

    final override fun validate(user: CommandUser<Sender>, value: T) {
        if (value.toDouble() !in min.toDouble()..max.toDouble()) {
            throw CommandException(defaultErrorMessage(user, value))
        }
    }
}
