package com.vexillum.plugincore.command.processor.validator

import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.managers.language.LanguageAgent

open class NumberRangeValidator<Sender : LanguageAgent, T : Number>(
    private val min: T,
    private val max: T,
    val invalidMessage: (Sender, T) -> String = { _, _ ->
        "The value must be between $min and $max"
    }
) : ArgumentValidator<Sender, T> {

    final override fun validate(sender: Sender, value: T) {
        if (value.toDouble() !in min.toDouble()..max.toDouble()) {
            throw CommandException(invalidMessage(sender, value))
        }
    }
}
