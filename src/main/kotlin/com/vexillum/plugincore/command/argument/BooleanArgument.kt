package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.BooleanExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent

open class BooleanArgument<Sender : LanguageAgent>(
    descriptor: (LanguageAgent) -> String,
    override val processor: ArgumentProcessor<Sender, Boolean, Boolean>? = null
) : Argument1<Sender, Boolean>() {

    override val extractor = BooleanExtractor<Sender>(descriptor)
}
