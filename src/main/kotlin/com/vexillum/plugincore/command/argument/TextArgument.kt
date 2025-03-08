package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.ArgumentExtractor
import com.vexillum.plugincore.command.extractor.StringExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message

open class TextArgument<Sender : LanguageAgent>(
    val descriptor: (CommandUser<*>) -> LanguageMessage,
    override val processor: ArgumentProcessor<Sender, String, String>? = null,
) : PlainArgument<Sender, String, String>() {

    override val extractor: ArgumentExtractor<Sender, String> = StringExtractor(descriptor)

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this(message(descriptor), processor)

    constructor(
        languageMessage: LanguageMessage,
        processor: ArgumentProcessor<Sender, String, String>? = null
    ) : this({ languageMessage }, processor)
}
