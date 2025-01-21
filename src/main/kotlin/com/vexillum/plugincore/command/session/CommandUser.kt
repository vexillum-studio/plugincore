package com.vexillum.plugincore.command.session

import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.context.LanguageState

interface CommandUser<Sender : LanguageAgent> : LanguageState<Sender, PluginCoreLanguage>
