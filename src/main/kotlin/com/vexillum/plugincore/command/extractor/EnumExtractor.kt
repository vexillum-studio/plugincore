package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message
import kotlin.reflect.KClass

open class EnumExtractor<Sender : LanguageAgent, T : Enum<T>>(
    enumClass: KClass<T>,
    override val descriptor: (CommandUser<*>) -> LanguageMessage,
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

    override fun defaultDescriptor(user: CommandUser<*>): LanguageMessage =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): LanguageMessage {
        val replacements = mapOf(
            "value" to value,
            "possibleValues" to valuesFromName.keys.joinToString()
        )
        return user.resolve(replacements) { command.parsing.enum }
    }

    override fun autocomplete(sender: Sender, value: String) =
        valueNames.map { Suggestion<Sender>(it) }
}
