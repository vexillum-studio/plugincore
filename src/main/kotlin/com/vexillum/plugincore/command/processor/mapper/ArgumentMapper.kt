package com.vexillum.plugincore.command.processor.mapper

import com.vexillum.plugincore.command.ArgumentMapException
import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.Message

interface ArgumentMapper<Sender : LanguageAgent, BaseType : Any, Type : Any> : ArgumentProcessor<Sender, BaseType, Type> {

    val clazz: Class<Type>

    val errorMessage: ((Sender, BaseType) -> String)?
        get() = { sender, value ->
            val replacements = mapOf(
                "value" to value,
                "type" to clazz.simpleName
            )
            sender.defaultCommandMessage(replacements) { command.transformMessage }
        }

    override fun process(sender: Sender, value: BaseType): Type =
        try {
            map(sender, value)
        } catch (e: Exception) {
            errorMessage?.let {
                throw CommandException(it(sender, value))
            } ?: throw ArgumentMapException(e)
        }

    fun map(sender: Sender, value: BaseType): Type

    fun Sender.defaultCommandMessage(
        replacements: Map<String, Any> = emptyMap(),
        block: PluginCoreLanguage.() -> Message
    ): String =
        PluginCoreLauncher.instance.resolve(this, replacements, block)
}
