package com.vexillum.plugincore.language.context

import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.MessageReplacements
import com.vexillum.plugincore.language.message.messageReplacements

internal class State<Agent : LanguageAgent, T : Language> (
    override val agent: Agent,
    override val value: T,
    override val replacements: MessageReplacements = messageReplacements()
) : LanguageState<Agent, T>
