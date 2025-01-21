package com.vexillum.plugincore.managers.language.context

import com.vexillum.plugincore.entities.Console.languageState
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.LocalLanguage

interface LanguageContext<T : Any> {

    fun language(localLanguage: LocalLanguage): Language<T>

    fun <Agent : LanguageAgent> languageState(
        agent: Agent
    ): LanguageState<Agent, T> =
        agent.languageState(this)
}
