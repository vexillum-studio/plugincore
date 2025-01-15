package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.launcher.defaultCommandMessage
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.PluginPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player

open class PluginPlayerExtractor<Sender : LanguageAgent, P : PluginPlayer>(
    override val descriptor: (LanguageAgent) -> String,
    val block: (Player) -> P?
) : BaseArgumentExtractor<Sender, P>() {

    override val extractor = { _: Sender, name: String ->
        block(Bukkit.getPlayer(name)!!)!!
    }

    override val errorMessage = { sender: Sender, name: String ->
        sender.defaultCommandMessage(mapOf("name" to name)) { command.parsing.player }
    }

    override fun autocomplete(sender: Sender, value: String) =
        Bukkit
            .getOnlinePlayers()
            .map { Suggestion<Sender>(it.name) }
}
