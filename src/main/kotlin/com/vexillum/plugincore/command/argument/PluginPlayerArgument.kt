package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.PluginPlayerExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message
import org.bukkit.entity.Player

open class PluginPlayerArgument<Sender : LanguageAgent, P : PluginPlayer>(
    descriptor: (CommandUser<*>) -> Message,
    playerSupplier: (Player) -> P?,
    override val processor: ArgumentProcessor<Sender, P, P>? = null
) : Argument1<Sender, P>() {

    constructor(
        descriptor: String,
        playerSupplier: (Player) -> P?,
        processor: ArgumentProcessor<Sender, P, P>? = null
    ) : this(message(descriptor), playerSupplier, processor)

    constructor(
        message: Message,
        playerSupplier: (Player) -> P?,
        processor: ArgumentProcessor<Sender, P, P>? = null
    ) : this({ message }, playerSupplier, processor)

    override val extractor = PluginPlayerExtractor<Sender, P>(descriptor, playerSupplier)
}
