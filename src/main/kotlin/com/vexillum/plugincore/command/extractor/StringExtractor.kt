package com.vexillum.plugincore.command.extractor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.managers.language.LanguageAgent

open class StringExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> String,
) : BaseArgumentExtractor<Sender, String>() {

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value
    }

    override fun defaultDescriptor(user: CommandUser<*>): String =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): String =
        value
}
