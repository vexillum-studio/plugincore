package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.PluginPlayerExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.entities.pluginPlayer
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message

open class PlayerArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> LanguageMessage,
    override val processor: ArgumentProcessor<Sender, PluginPlayer, PluginPlayer>? = null
) : Argument1<Sender, PluginPlayer>() {

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, PluginPlayer, PluginPlayer>? = null
    ) : this(message(descriptor), processor)

    constructor(
        languageMessage: LanguageMessage,
        processor: ArgumentProcessor<Sender, PluginPlayer, PluginPlayer>? = null
    ) : this({ languageMessage }, processor)

    override val extractor = PluginPlayerExtractor<Sender, PluginPlayer>(descriptor) { it.pluginPlayer() }
}
