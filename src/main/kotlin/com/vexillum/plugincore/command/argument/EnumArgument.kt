package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.EnumExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message
import kotlin.reflect.KClass

open class EnumArgument<Sender : LanguageAgent, T : Enum<T>>(
    descriptor: (CommandUser<*>) -> LanguageMessage,
    enumClass: KClass<T>,
    override val processor: ArgumentProcessor<Sender, T, T>? = null
) : Argument1<Sender, T>() {

    constructor(
        descriptor: String,
        enumClass: KClass<T>,
        processor: ArgumentProcessor<Sender, T, T>? = null
    ) : this(message(descriptor), enumClass, processor)

    constructor(
        languageMessage: LanguageMessage,
        enumClass: KClass<T>,
        processor: ArgumentProcessor<Sender, T, T>? = null
    ) : this({ languageMessage }, enumClass, processor)

    override val extractor = EnumExtractor<Sender, T>(enumClass, descriptor)
}
