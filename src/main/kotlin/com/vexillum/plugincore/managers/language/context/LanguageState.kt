package com.vexillum.plugincore.managers.language.context

import com.vexillum.plugincore.command.LanguageException
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.Message
import com.vexillum.plugincore.managers.language.resolve

interface LanguageState<Agent : LanguageAgent, T : Any> {

    val agent: Agent
    val language: Language<T>

    fun resolve(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): String =
        language.resolve(replacements, block)

    fun sendMessage(
        replacements: Map<String, Any>,
        block: T.() -> Message
    ) {
        agent.sendMessage(resolve(replacements, block))
    }

    fun languageException(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): Nothing =
        throw LanguageException(resolve(replacements, block))
}
