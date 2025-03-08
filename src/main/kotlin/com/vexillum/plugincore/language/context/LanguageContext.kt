package com.vexillum.plugincore.language.context

import com.vexillum.plugincore.entities.Console.languageState
import com.vexillum.plugincore.extensions.PluginCoreExtensions
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageException
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.Message

interface LanguageContext<T : Any> : PluginCoreExtensions {

    fun language(localLanguage: LocalLanguage): Language<T>

    fun <Agent : LanguageAgent> languageState(
        agent: Agent
    ): LanguageState<Agent, T> =
        agent.languageState(this)

    fun <Agent : LanguageAgent> Agent.resolve(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): LanguageMessage =
        languageState(this).resolve(replacements, block)

    fun <Agent : LanguageAgent> Agent.sendMessage(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) =
        sendMessage(resolve(replacements, block))

    fun <Agent : LanguageAgent> Agent.languageException(
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): Nothing =
        throw LanguageException(resolve(replacements, block))
}
