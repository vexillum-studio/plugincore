package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.DoubleExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent

open class DoubleArgument<Sender : LanguageAgent>(
    descriptor: (LanguageAgent) -> String,
    override val processor: ArgumentProcessor<Sender, Double, Double>? = null
) : Argument1<Sender, Double>() {

    override val extractor = DoubleExtractor<Sender>(descriptor)
}
