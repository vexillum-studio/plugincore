package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.context.LanguageState
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage

typealias CommandName = String

interface Command<Sender : LanguageAgent> {
    val startToken: String get() = SLASH
    val name: CommandName
    val aliases: Set<CommandName>
    val description: ((LanguageAgent) -> String)?
    val permission: String?
    val usages: List<CommandUsage<Sender>>
    val subCommands: Set<Command<Sender>>
    fun matches(label: String): Boolean
    fun execute(session: CommandSession<Sender>)
    fun autocomplete(session: CommandSession<Sender>): MutableList<String>
    fun <A : LanguageAgent> A.commandException(
        block: LanguageState<A, PluginCoreLanguage>.() -> LanguageMessage
    ): Nothing
    fun <A : LanguageAgent> A.commandMessage(
        block: LanguageState<A, PluginCoreLanguage>.() -> LanguageMessage
    )

    companion object {
        const val SLASH = "/"
        const val DOUBLE_SLASH = "//"
    }
}
