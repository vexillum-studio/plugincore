package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.IntExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message

open class IntArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> LanguageMessage,
    override val processor: ArgumentProcessor<Sender, Int, Int>? = null
) : Argument1<Sender, Int>() {

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, Int, Int>? = null
    ) : this(message(descriptor), processor)

    constructor(
        languageMessage: LanguageMessage,
        processor: ArgumentProcessor<Sender, Int, Int>? = null
    ) : this({ languageMessage }, processor)

    override val extractor = IntExtractor<Sender>(descriptor)
}
