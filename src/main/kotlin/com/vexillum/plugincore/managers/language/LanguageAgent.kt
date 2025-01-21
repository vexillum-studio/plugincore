package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.managers.language.context.LanguageContext
import com.vexillum.plugincore.managers.language.context.LanguageState
import com.vexillum.plugincore.managers.language.context.State
import org.bukkit.command.CommandSender
import org.bukkit.conversations.Conversable

interface LanguageAgent : CommandSender, Conversable {

    val activeLanguage: LocalLanguage?

    val localLanguage get() = activeLanguage ?: LocalLanguage.DEFAULT

    fun <Agent : LanguageAgent, T : Any> Agent.languageState(
        context: LanguageContext<T>
    ): LanguageState<Agent, T> =
        State(this, context.language(localLanguage))
}
