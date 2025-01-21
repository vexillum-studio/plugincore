package com.vexillum.plugincore.command.processor.validator

import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.managers.language.LanguageAgent

interface ArgumentValidator<Sender : LanguageAgent, Type : Any> : ArgumentProcessor<Sender, Type, Type> {

    override fun process(user: CommandUser<Sender>, value: Type): Type = value

    fun validate(user: CommandUser<Sender>, value: Type)
}
