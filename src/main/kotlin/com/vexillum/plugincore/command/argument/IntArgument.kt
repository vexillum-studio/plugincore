package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.IntExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent

open class IntArgument<Sender : LanguageAgent>(
    descriptor: (LanguageAgent) -> String,
    override val processor: ArgumentProcessor<Sender, Int, Int>? = null
) : Argument1<Sender, Int>() {

    override val extractor = IntExtractor<Sender>(descriptor)
}
