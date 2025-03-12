package com.vexillum.plugincore.command.processor.mapper

import com.vexillum.plugincore.command.ArgumentMapException
import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message

interface ArgumentMapper<Sender : LanguageAgent, BaseType : Any, Type : Any> : ArgumentProcessor<Sender, BaseType, Type> {

    val clazz: Class<Type>

    val errorMessage: ((CommandUser<*>, BaseType) -> Message)?
        get() = { user, value ->
            user.resolve { command.transformMessage }.replacing(
                "value" to value,
                "type" to clazz.simpleName
            )
        }

    override fun process(user: CommandUser<Sender>, value: BaseType): Type =
        try {
            map(user, value)
        } catch (e: CommandException) {
            errorMessage?.let { message ->
                throw CommandException(message(user, value))
            } ?: throw ArgumentMapException(e)
        }

    fun map(user: CommandUser<Sender>, value: BaseType): Type
}
