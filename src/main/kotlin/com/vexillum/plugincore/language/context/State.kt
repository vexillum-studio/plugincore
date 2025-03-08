package com.vexillum.plugincore.language.context

import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent

internal class State<Agent : LanguageAgent, T : Any> (
    override val agent: Agent,
    override val language: Language<T>
) : LanguageState<Agent, T>
