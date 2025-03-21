package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message
import kotlin.reflect.KClass

open class EnumExtractor<Sender : LanguageAgent, T : Enum<T>>(
    enumClass: KClass<T>,
    override val descriptor: (CommandUser<*>) -> Message,
    private val ignoreCase: Boolean = true
) : BaseArgumentExtractor<Sender, T>() {

    private val values = enumClass.java.enumConstants

    private val valueNames = values.map { message(it.name) }

    private val valuesFromName = values.associateBy {
        if (ignoreCase) it.name.lowercase() else it.name
    }

    override val extractor = { _: CommandUser<Sender>, value: String ->
        val enumName = if (ignoreCase) value.lowercase() else value
        valuesFromName.getValue(enumName)
    }

    override fun defaultDescriptor(user: CommandUser<*>): Message =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): Message =
        user.resolve { command.parsing.enum }.replacing(
            "value" to value,
            "possibleValues" to valuesFromName.keys.joinToString()
        )

    override fun autocomplete(sender: Sender, value: String) =
        valueNames.map { Suggestion<Sender>(it) }
}
