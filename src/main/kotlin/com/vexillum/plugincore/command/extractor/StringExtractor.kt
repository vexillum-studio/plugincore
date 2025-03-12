package com.vexillum.plugincore.command.extractor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message

open class StringExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> Message,
) : BaseArgumentExtractor<Sender, String>() {

    override val extractor = { _: CommandUser<Sender>, value: String ->
        value
    }

    override fun defaultDescriptor(user: CommandUser<*>): Message =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): Message =
        descriptor(user)
}
