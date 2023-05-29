package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.StringExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.util.Constants.SPACE

open class TextArgument<Sender : LanguageAgent>(
    val descriptor: (LanguageAgent) -> String,
    override val processor: ArgumentProcessor<Sender, String, String>? = null
) : ArgumentN<Sender, String, String>() {

    override val extractor = StringExtractor<Sender>(descriptor)

    override val merger = { _: Sender, words: Iterator<String> ->
        words
            .asSequence()
            .joinToString(separator = SPACE)
    }
}
