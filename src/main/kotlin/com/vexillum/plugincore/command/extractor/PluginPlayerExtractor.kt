package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.managers.language.PluginPlayer
import org.bukkit.Bukkit
import java.util.UUID

open class PluginPlayerExtractor<Sender : LanguageAgent, P : PluginPlayer>(
    override val descriptor: (LanguageAgent) -> String,
    val block: (UUID) -> P?
) : ArgumentExtractor<Sender, P> {

    override val extractor = { _: Sender, name: String ->
        block(Bukkit.getPlayer(name)!!.uniqueId)!!
    }

    override val errorMessage = { sender: Sender, name: String ->
        sender.defaultCommandMessage(mapOf("name" to name)) { command.parsing.player }
    }

    override fun autocomplete(sender: Sender, value: String): List<String> =
        Bukkit
            .getOnlinePlayers()
            .map { it.name }
            .sorted()
}
