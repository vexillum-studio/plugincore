package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.WorldExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message
import org.bukkit.World

open class WorldArgument<Sender : LanguageAgent>(
    val descriptor: (CommandUser<*>) -> Message,
    override val processor: ArgumentProcessor<Sender, World, World>? = null
) : Argument1<Sender, World>() {

    override val extractor = WorldExtractor<Sender>(descriptor)

    constructor(
        descriptor: String,
        processor: ArgumentProcessor<Sender, World, World>? = null
    ) : this(message(descriptor), processor)

    constructor(
        message: Message,
        processor: ArgumentProcessor<Sender, World, World>? = null
    ) : this({ message }, processor)
}
