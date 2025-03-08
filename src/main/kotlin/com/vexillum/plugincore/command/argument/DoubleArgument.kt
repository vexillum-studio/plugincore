package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.DoubleExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message

open class DoubleArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> LanguageMessage,
    override val processor: ArgumentProcessor<Sender, Double, Double>? = null
) : Argument1<Sender, Double>() {

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, Double, Double>? = null
    ) : this(message(descriptor), processor)

    constructor(
        languageMessage: LanguageMessage,
        processor: ArgumentProcessor<Sender, Double, Double>? = null
    ) : this({ languageMessage }, processor)

    override val extractor = DoubleExtractor<Sender>(descriptor)
}
