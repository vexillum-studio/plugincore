package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.ArgumentExtractor
import com.vexillum.plugincore.command.extractor.StringExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message

open class TextArgument<Sender : LanguageAgent>(
    val descriptor: (CommandUser<*>) -> Message,
    override val processor: ArgumentProcessor<Sender, String, String>? = null,
) : PlainArgument<Sender, String, String>() {

    override val extractor: ArgumentExtractor<Sender, String> = StringExtractor(descriptor)

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this(message(descriptor), processor)

    constructor(
        message: Message,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this({ message }, processor)
}
