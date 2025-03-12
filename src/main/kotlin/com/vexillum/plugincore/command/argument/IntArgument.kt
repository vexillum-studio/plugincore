package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.IntExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message

open class IntArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> Message,
    override val processor: ArgumentProcessor<Sender, Int, Int>? = null
) : Argument1<Sender, Int>() {

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, Int, Int>? = null
    ) : this(message(descriptor), processor)

    constructor(
        message: Message,
        processor: ArgumentProcessor<Sender, Int, Int>? = null
    ) : this({ message }, processor)

    override val extractor = IntExtractor<Sender>(descriptor)
}
