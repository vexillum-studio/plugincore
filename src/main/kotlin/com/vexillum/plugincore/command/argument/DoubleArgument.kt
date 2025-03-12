package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.DoubleExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message

open class DoubleArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> Message,
    override val processor: ArgumentProcessor<Sender, Double, Double>? = null
) : Argument1<Sender, Double>() {

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, Double, Double>? = null
    ) : this(message(descriptor), processor)

    constructor(
        message: Message,
        processor: ArgumentProcessor<Sender, Double, Double>? = null
    ) : this({ message }, processor)

    override val extractor = DoubleExtractor<Sender>(descriptor)
}
