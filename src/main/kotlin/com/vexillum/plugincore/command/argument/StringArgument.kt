package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.StringExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message

open class StringArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> Message,
    override val processor: ArgumentProcessor<Sender, String, String>? = null
) : Argument1<Sender, String>() {

    override val extractor = StringExtractor<Sender>(descriptor)

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this(message(descriptor), processor)

    constructor(
        message: Message,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this({ message }, processor)
}
