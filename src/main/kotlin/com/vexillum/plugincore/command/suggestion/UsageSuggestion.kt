package com.vexillum.plugincore.command.suggestion

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

class UsageSuggestion<Sender : LanguageAgent>(
    value: LanguageMessage
) : BaseCommandSuggestion<Sender>(value) {

    override val priority = 2

    override val matchable = false

    override fun describe(user: CommandUser<*>): LanguageMessage =
        with(user) {
            val color = resolve { command.descriptor.color }
            val prefix = resolve { command.descriptor.prefix }
            val postfix = resolve { command.descriptor.postfix }
            color + prefix + message + postfix
        }
}
