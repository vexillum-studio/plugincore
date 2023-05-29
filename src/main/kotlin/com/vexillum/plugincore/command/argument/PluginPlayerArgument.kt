package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.PluginPlayerExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.PluginPlayer
import java.util.UUID

open class PluginPlayerArgument<Sender : LanguageAgent, P : PluginPlayer>(
    descriptor: (LanguageAgent) -> String,
    playerSupplier: (UUID) -> P?,
    override val processor: ArgumentProcessor<Sender, P, P>? = null
) : Argument1<Sender, P>() {

    override val extractor = PluginPlayerExtractor<Sender, P>(descriptor, playerSupplier)
}
