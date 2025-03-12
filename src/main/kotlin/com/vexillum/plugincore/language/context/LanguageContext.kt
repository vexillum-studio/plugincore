package com.vexillum.plugincore.language.context

import com.vexillum.plugincore.entities.Console.languageState
import com.vexillum.plugincore.extensions.PluginCoreExtensions
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageException
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.LocaleTranslation
import com.vexillum.plugincore.language.message.Message

interface LanguageContext<T : Language> : PluginCoreExtensions {

    fun translation(localLanguage: LocalLanguage): LocaleTranslation<T>

    fun <Agent : LanguageAgent> languageState(
        agent: Agent
    ): LanguageState<Agent, T> =
        agent.languageState(this)

    fun <Agent : LanguageAgent> Agent.resolve(
        block: T.() -> Message
    ): Message =
        languageState(this).resolve(block)

    fun <Agent : LanguageAgent> Agent.sendMessage(
        block: T.() -> Message
    ) =
        sendMessage(resolve(block))

    fun <Agent : LanguageAgent> Agent.languageException(
        block: T.() -> Message
    ): Nothing =
        throw LanguageException(resolve(block))
}
