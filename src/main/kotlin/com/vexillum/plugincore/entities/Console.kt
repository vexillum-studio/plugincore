package com.vexillum.plugincore.entities

import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.LocalLanguage
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender

private val consoleSender = Bukkit.getConsoleSender()

object Console : LanguageAgent, ConsoleCommandSender by consoleSender {
    override val activeLanguage: LocalLanguage = ENGLISH
}
