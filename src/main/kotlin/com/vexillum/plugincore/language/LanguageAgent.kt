package com.vexillum.plugincore.language

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.extensions.tryCastOrNull
import com.vexillum.plugincore.language.context.LanguageContext
import com.vexillum.plugincore.language.context.LanguageState
import com.vexillum.plugincore.language.context.State
import com.vexillum.plugincore.language.message.Message
import org.bukkit.command.CommandSender
import org.bukkit.conversations.Conversable

interface LanguageAgent : CommandSender, Conversable {

    val activeLanguage: LocalLanguage?

    val localLanguage get() = activeLanguage ?: LocalLanguage.DEFAULT

    var currentCommandSession: CommandSession<*>?

    fun <A : LanguageAgent> A.commandSession(): CommandSession<A>? =
        currentCommandSession.tryCastOrNull<CommandSession<A>>()

    fun sendMessage(message: Message): Unit =
        sendMessage(message.resolved())

    fun <A : LanguageAgent, T : Language> A.languageState(
        context: LanguageContext<T>
    ): LanguageState<A, T> =
        State(this, context.translation(localLanguage).value)
}
