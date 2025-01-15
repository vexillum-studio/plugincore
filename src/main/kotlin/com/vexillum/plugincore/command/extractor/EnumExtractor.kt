package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.launcher.defaultCommandMessage
import com.vexillum.plugincore.managers.language.LanguageAgent
import kotlin.reflect.KClass

open class EnumExtractor<Sender : LanguageAgent, T : Enum<T>>(
    enumClass: KClass<T>,
    override val descriptor: (LanguageAgent) -> String,
    private val ignoreCase: Boolean = true
) : BaseArgumentExtractor<Sender, T>() {

    private val values = enumClass.java.enumConstants

    private val valueNames = values.map { it.name }

    private val valuesFromName = values.associateBy {
        if (ignoreCase) it.name.lowercase() else it.name
    }

    override val extractor = { _: Sender, value: String ->
        val enumName = if (ignoreCase) value.lowercase() else value
        valuesFromName.getValue(enumName)
    }

    override val errorMessage = { sender: Sender, value: String ->
        val replacements = mapOf(
            "value" to value,
            "possibleValues" to valuesFromName.keys.joinToString()
        )
        sender.defaultCommandMessage(replacements) { command.parsing.enum }
    }

    override fun autocomplete(sender: Sender, value: String) =
        valueNames.map { Suggestion<Sender>(it) }
}
