package com.vexillum.plugincore.command.processor.mapper

import com.vexillum.plugincore.command.ArgumentMapException
import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

interface ArgumentMapper<Sender : LanguageAgent, BaseType : Any, Type : Any> : ArgumentProcessor<Sender, BaseType, Type> {

    val clazz: Class<Type>

    val errorMessage: ((CommandUser<*>, BaseType) -> LanguageMessage)?
        get() = { user, value ->
            val replacements = mapOf(
                "value" to value,
                "type" to clazz.simpleName
            )
            user.resolve(replacements) { command.transformMessage }
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
