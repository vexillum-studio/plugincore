package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.PluginPlayerExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.PluginPlayer
import com.vexillum.plugincore.managers.language.pluginPlayer

open class PlayerArgument<Sender : LanguageAgent>(
    descriptor: (LanguageAgent) -> String,
    override val processor: ArgumentProcessor<Sender, PluginPlayer, PluginPlayer>? = null
) : Argument1<Sender, PluginPlayer>() {

    override val extractor = PluginPlayerExtractor<Sender, PluginPlayer>(descriptor) { it.pluginPlayer() }
}
