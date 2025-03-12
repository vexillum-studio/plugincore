package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.BooleanExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message

open class BooleanArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> Message,
    override val processor: ArgumentProcessor<Sender, Boolean, Boolean>? = null
) : Argument1<Sender, Boolean>() {

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, Boolean, Boolean>? = null
    ) : this(message(descriptor), processor)

    constructor(
        message: Message,
        processor: ArgumentProcessor<Sender, Boolean, Boolean>? = null
    ) : this({ message }, processor)

    override val extractor = BooleanExtractor<Sender>(descriptor)
}
