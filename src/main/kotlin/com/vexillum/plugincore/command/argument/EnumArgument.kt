package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.EnumExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.managers.language.LanguageAgent
import kotlin.reflect.KClass

open class EnumArgument<Sender : LanguageAgent, T : Enum<T>>(
    descriptor: (CommandUser<*>) -> String,
    enumClass: KClass<T>,
    override val processor: ArgumentProcessor<Sender, T, T>? = null
) : Argument1<Sender, T>() {

    override val extractor = EnumExtractor<Sender, T>(enumClass, descriptor)
}
