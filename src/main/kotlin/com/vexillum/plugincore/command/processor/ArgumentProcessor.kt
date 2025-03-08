package com.vexillum.plugincore.command.processor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent

interface ArgumentProcessor<Sender : LanguageAgent, BaseType : Any, Type : Any> {

    fun process(user: CommandUser<Sender>, value: BaseType): Type

    operator fun <FinalType : Any> plus(
        processor: ArgumentProcessor<Sender, Type, FinalType>
    ): ArgumentProcessor<Sender, BaseType, FinalType> =
        CompoundArgumentProcessor(this, processor)

    /*
    operator fun plus(
        other: ArgumentProcessor<Sender, Type, Type>?
    ): ArgumentProcessor<Sender, BaseType, Type> =
        if (other != null) plus(other) else this*/
}
