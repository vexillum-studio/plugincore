package com.vexillum.plugincore.managers.language.context

import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.Message

interface LanguageAgentContext<T : Any> : LanguageContext<T>, LanguageAgent {

    fun resolve(
        replacements: Map<String, Any> = emptyMap(),
        messageBlock: T.() -> Message
    ): String

    fun commandException(
        replacements: Map<String, Any> = emptyMap(),
        messageBlock: T.() -> Message
    ): String
}
