package com.vexillum.plugincore.language

import com.vexillum.plugincore.language.context.LanguageContext
import com.vexillum.plugincore.language.context.LanguageState
import com.vexillum.plugincore.language.context.State
import org.bukkit.command.CommandSender
import org.bukkit.conversations.Conversable

interface LanguageAgent : CommandSender, Conversable {

    val activeLanguage: LocalLanguage?

    val localLanguage get() = activeLanguage ?: LocalLanguage.DEFAULT

    fun sendMessage(message: LanguageMessage): Unit =
        sendMessage(message.resolved())

    fun <A : LanguageAgent, T : Any> A.languageState(
        context: LanguageContext<T>
    ): LanguageState<A, T> =
        State(this, context.language(localLanguage))
}
