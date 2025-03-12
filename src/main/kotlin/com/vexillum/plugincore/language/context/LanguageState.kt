package com.vexillum.plugincore.language.context

import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageException
import com.vexillum.plugincore.language.LocaleResolver
import com.vexillum.plugincore.language.message.Message

interface LanguageState<Agent : LanguageAgent, T : Language> : LocaleResolver<T> {

    val agent: Agent
    override val value: T

    fun sendMessage(
        block: T.() -> Message
    ) {
        sendMessage(resolve(block))
    }

    fun sendMessage(
        message: Message
    ) {
        agent.sendMessage(message())
    }

    fun languageException(
        block: T.() -> Message
    ): Nothing =
        throw LanguageException(resolve(block))
}
