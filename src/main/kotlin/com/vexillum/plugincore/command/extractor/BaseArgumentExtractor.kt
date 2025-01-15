package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.managers.language.LanguageAgent

abstract class BaseArgumentExtractor<Sender : LanguageAgent, Type : Any> : ArgumentExtractor<Sender, Type> {

    override fun toString() =
        describe(pluginCoreInstance.languageScope())
}
