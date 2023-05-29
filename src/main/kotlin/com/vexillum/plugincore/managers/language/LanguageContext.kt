package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.command.LanguageException

interface LanguageContext<T : Any> {

    fun language(localLanguage: LocalLanguage): Language<T>

    fun resolve(
        languageAgent: LanguageAgent,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) =
        language(languageAgent.localLanguage).resolve(replacements, block)

    fun sendMessage(
        languageAgent: LanguageAgent,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) =
        languageAgent.sendMessage(this, replacements, block)

    fun commandException(
        languageAgent: LanguageAgent,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): Nothing =
        throw LanguageException(resolve(languageAgent, replacements, block))

    interface LanguageContextScope<T : Any> {

        fun resolve(
            replacements: Map<String, Any> = emptyMap(),
            messageBlock: T.() -> Message
        ): String

        fun sendMessage(
            replacements: Map<String, Any> = emptyMap(),
            messageBlock: T.() -> Message
        )

        fun commandException(
            replacements: Map<String, Any> = emptyMap(),
            messageBlock: T.() -> Message
        ): String
    }

    fun <A : LanguageAgent, R> withAgent(
        agent: A,
        block: LanguageContextScope<T>.() -> R
    ): R {

        val scope = object : LanguageContextScope<T> {

            override fun resolve(
                replacements: Map<String, Any>,
                messageBlock: T.() -> Message
            ): String =
                resolve(agent, replacements, messageBlock)

            override fun sendMessage(
                replacements: Map<String, Any>,
                messageBlock: T.() -> Message
            ) =
                sendMessage(agent, replacements, messageBlock)

            override fun commandException(
                replacements: Map<String, Any>,
                messageBlock: T.() -> Message
            ): String =
                commandException(agent, replacements, messageBlock)
        }

        return scope.run(block)
    }
}
