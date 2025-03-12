package com.vexillum.plugincore.language.context

import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent

internal class State<Agent : LanguageAgent, T : Language> (
    override val agent: Agent,
    override val value: T
) : LanguageState<Agent, T>
