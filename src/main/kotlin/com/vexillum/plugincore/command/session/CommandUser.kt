package com.vexillum.plugincore.command.session

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.context.LanguageState
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage

interface CommandUser<Sender : LanguageAgent> : LanguageState<Sender, PluginCoreLanguage>
