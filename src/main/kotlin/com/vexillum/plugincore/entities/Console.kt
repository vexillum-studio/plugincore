package com.vexillum.plugincore.entities

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.LocalLanguage.ENGLISH
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender

private val consoleSender = Bukkit.getConsoleSender()

object Console : LanguageAgent, ConsoleCommandSender by consoleSender {
    override val activeLanguage: LocalLanguage = ENGLISH

    override var currentCommandSession: CommandSession<*>? = null
}
