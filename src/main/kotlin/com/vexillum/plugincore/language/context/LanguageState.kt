package com.vexillum.plugincore.language.context

import com.vexillum.plugincore.entities.BukkitConsole.languageState
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageException
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.Message
import com.vexillum.plugincore.language.resolve

interface LanguageState<Agent : LanguageAgent, T : Any> {

    val agent: Agent
    val language: Language<T>

    fun resolve(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): LanguageMessage =
        language.resolve(replacements, block)

    fun sendMessage(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) {
        sendMessage(resolve(replacements, block))
    }

    fun sendMessage(
        languageMessage: LanguageMessage,
    ) {
        agent.sendMessage(languageMessage())
    }

    fun languageException(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): Nothing =
        throw LanguageException(resolve(replacements, block))

    fun <O : Any, R> compose(
        context: LanguageContext<O>,
        block: LanguageState<Agent, O>.() -> R
    ): R =
        agent.languageState(context).run(block)
}
