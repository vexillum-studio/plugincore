package com.vexillum.plugincore.command.session

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.context.DefaultState

interface CommandUser<Sender : LanguageAgent> : DefaultState<Sender>
