package com.vexillum.plugincore.extensions

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage

interface PluginCoreExtensions {

    val pluginCore: PluginCore

    fun <A : LanguageAgent> A.prefixedMessage(
        block: A.() -> LanguageMessage
    ): LanguageMessage =
        languageState(pluginCore).run {
            val prefix = resolve(
                mapOf("pluginName" to pluginCore.plugin.name)
            ) { prefix }
            prefix + resolve { color } + block()
        }

    fun <A : LanguageAgent> A.sendPrefixedMessage(
        block: A.() -> LanguageMessage
    ): Unit =
        sendMessage(prefixedMessage(block))
}
