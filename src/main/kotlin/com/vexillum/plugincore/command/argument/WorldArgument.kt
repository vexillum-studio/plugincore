package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.WorldExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.World

open class WorldArgument<Sender : LanguageAgent>(
    val descriptor: (LanguageAgent) -> String,
    override val processor: ArgumentProcessor<Sender, World, World>? = null
) : Argument1<Sender, World>() {

    override val extractor = WorldExtractor<Sender>(descriptor)
}
