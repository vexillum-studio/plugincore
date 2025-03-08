package com.vexillum.plugincore.command.processor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent

class CompoundArgumentProcessor<Sender : LanguageAgent, BaseType : Any, TransitionType : Any, Type : Any>(
    private val baseProcessor: ArgumentProcessor<Sender, BaseType, TransitionType>,
    private val topProcessor: ArgumentProcessor<Sender, TransitionType, Type>
) : ArgumentProcessor<Sender, BaseType, Type> {

    override fun process(user: CommandUser<Sender>, value: BaseType): Type =
        baseProcessor.process(user, value).let { baseResult ->
            topProcessor.process(user, baseResult)
        }
}
