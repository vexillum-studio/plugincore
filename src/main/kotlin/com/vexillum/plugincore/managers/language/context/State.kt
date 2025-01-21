package com.vexillum.plugincore.managers.language.context

import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LanguageAgent

internal class State<Agent : LanguageAgent, T : Any> (
    override val agent: Agent,
    override val language: Language<T>
) : LanguageState<Agent, T>
