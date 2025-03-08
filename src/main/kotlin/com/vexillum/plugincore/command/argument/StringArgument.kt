package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.StringExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message

open class StringArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> LanguageMessage,
    override val processor: ArgumentProcessor<Sender, String, String>? = null
) : Argument1<Sender, String>() {

    override val extractor = StringExtractor<Sender>(descriptor)

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this(message(descriptor), processor)

    constructor(
        languageMessage: LanguageMessage,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this({ languageMessage }, processor)
}
