package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.StringExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent

open class StringArgument<Sender : LanguageAgent>(
    descriptor: (LanguageAgent) -> String,
    override val processor: ArgumentProcessor<Sender, String, String>? = null
) : Argument1<Sender, String>() {

    override val extractor = StringExtractor<Sender>(descriptor)
}
