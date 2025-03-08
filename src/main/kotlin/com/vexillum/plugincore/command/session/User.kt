package com.vexillum.plugincore.command.session

import com.vexillum.plugincore.entities.BukkitConsole.languageState
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.context.LanguageState
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage

internal open class User<Sender : LanguageAgent>(
    state: LanguageState<Sender, PluginCoreLanguage>
) : CommandUser<Sender>, LanguageState<Sender, PluginCoreLanguage> by state {

    constructor(sender: Sender) : this(sender.languageState(pluginCoreInstance))
}
