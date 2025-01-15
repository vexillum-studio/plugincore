package com.vexillum.plugincore.managers.language.context

import com.vexillum.plugincore.command.LanguageException
import com.vexillum.plugincore.managers.language.Console
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.LocalLanguage
import com.vexillum.plugincore.managers.language.Message
import com.vexillum.plugincore.managers.language.resolve

interface LanguageContext<T : Any> {

    fun language(localLanguage: LocalLanguage): Language<T>

    fun resolve(
        agent: LanguageAgent,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) =
        language(agent.localLanguage).resolve(replacements, block)

    fun sendMessage(
        agent: LanguageAgent,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) =
        agent.sendMessage(this, replacements, block)

    fun commandException(
        agent: LanguageAgent,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): Nothing =
        throw LanguageException(resolve(agent, replacements, block))

    fun <R> withAgent(
        agent: LanguageAgent,
        block: LanguageAgentContext<T>.() -> R
    ): R {
        return languageScope(agent).run(block)
    }

    fun languageScope(
        agent: LanguageAgent = Console
    ): LanguageAgentContext<T> {
        val scope = object : LanguageAgentContext<T>, LanguageAgent by agent {

            override fun resolve(
                replacements: Map<String, Any>,
                messageBlock: T.() -> Message
            ): String =
                resolve(agent, replacements, messageBlock)

            override fun commandException(
                replacements: Map<String, Any>,
                messageBlock: T.() -> Message
            ): String =
                commandException(agent, replacements, messageBlock)

            override fun language(localLanguage: LocalLanguage): Language<T> =
                this@LanguageContext.language(localLanguage)

            override fun <R> withAgent(
                agent: LanguageAgent,
                block: LanguageAgentContext<T>.() -> R
            ): R =
                this@LanguageContext.withAgent(agent, block)
        }
        return scope
    }
}
