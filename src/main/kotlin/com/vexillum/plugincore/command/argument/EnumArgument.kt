package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.EnumExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message
import kotlin.reflect.KClass

open class EnumArgument<Sender : LanguageAgent, T : Enum<T>>(
    descriptor: (CommandUser<*>) -> Message,
    enumClass: KClass<T>,
    override val processor: ArgumentProcessor<Sender, T, T>? = null
) : Argument1<Sender, T>() {

    constructor(
        descriptor: String,
        enumClass: KClass<T>,
        processor: ArgumentProcessor<Sender, T, T>? = null
    ) : this(message(descriptor), enumClass, processor)

    constructor(
        message: Message,
        enumClass: KClass<T>,
        processor: ArgumentProcessor<Sender, T, T>? = null
    ) : this({ message }, enumClass, processor)

    override val extractor = EnumExtractor<Sender, T>(enumClass, descriptor)
}
