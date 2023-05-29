package com.vexillum.plugincore.command.processor

import com.vexillum.plugincore.managers.language.LanguageAgent

class CompoundArgumentProcessor<Sender : LanguageAgent, BaseType : Any, TransitionType : Any, Type : Any>(
    private val baseProcessor: ArgumentProcessor<Sender, BaseType, TransitionType>,
    private val topProcessor: ArgumentProcessor<Sender, TransitionType, Type>
) : ArgumentProcessor<Sender, BaseType, Type> {

    override fun process(sender: Sender, value: BaseType): Type =
        baseProcessor.process(sender, value).let { baseResult ->
            topProcessor.process(sender, baseResult)
        }
}
