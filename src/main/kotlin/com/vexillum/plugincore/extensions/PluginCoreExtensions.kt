package com.vexillum.plugincore.extensions

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.context.DefaultState
import com.vexillum.plugincore.language.message.Message

interface PluginCoreExtensions {

    val pluginCore: PluginCore

    fun <A : LanguageAgent> A.defaultState(
        block: DefaultState<A>.() -> Message
    ): Message =
        languageState(pluginCore).block()

    fun <A : LanguageAgent> A.prefixedMessage(
        block: A.() -> Message
    ): Message =
        languageState(pluginCore).run {
            val prefix = resolve { prefix }.replace("pluginName", pluginCore.plugin.name)
            prefix + resolve { color } + block()
        }

    fun <A : LanguageAgent> A.sendPrefixedMessage(
        block: A.() -> Message
    ): Unit =
        sendMessage(prefixedMessage(block))
}
