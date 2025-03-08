package com.vexillum.plugincore.command.processor.validator

import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

interface ArgumentValidator<Sender : LanguageAgent, Type : Any> : ArgumentProcessor<Sender, Type, Type> {

    override fun process(user: CommandUser<Sender>, value: Type): Type = value

    fun defaultErrorMessage(user: CommandUser<*>, value: Type): LanguageMessage

    fun validate(user: CommandUser<Sender>, value: Type)
}
