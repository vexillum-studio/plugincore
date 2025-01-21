package com.vexillum.plugincore.command.session

import com.vexillum.plugincore.entities.BukkitConsole.languageState
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.context.LanguageState

internal open class User<Sender : LanguageAgent>(
    state: LanguageState<Sender, PluginCoreLanguage>
) : CommandUser<Sender>, LanguageState<Sender, PluginCoreLanguage> by state {

    constructor(sender: Sender) : this(sender.languageState(pluginCoreInstance))
}
