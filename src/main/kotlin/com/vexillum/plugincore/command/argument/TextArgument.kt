package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.managers.language.LanguageAgent

open class TextArgument<Sender : LanguageAgent>(
    val descriptor: (CommandUser<*>) -> String,
    override val processor: ArgumentProcessor<Sender, String, String>? = null
) : PlainArgument<Sender, String, String>() {

    override val transform = { _: CommandUser<Sender>, value: String ->
        value
    }
}
