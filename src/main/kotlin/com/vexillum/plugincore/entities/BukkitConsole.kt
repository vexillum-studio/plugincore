package com.vexillum.plugincore.entities

import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.LocalLanguage.ENGLISH
import org.bukkit.Bukkit
import org.bukkit.command.ConsoleCommandSender

private val consoleSender = Bukkit.getConsoleSender()

object BukkitConsole : LanguageAgent, ConsoleCommandSender by consoleSender {

    override val activeLanguage: LocalLanguage
        get() = ENGLISH // TODO Change to config on plugincore launcher
}
