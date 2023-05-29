package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.Bukkit
import org.bukkit.World

open class WorldExtractor<Sender : LanguageAgent>(
    override val descriptor: (LanguageAgent) -> String
) : ArgumentExtractor<Sender, World> {

    override val extractor = { _: Sender, value: String ->
        Bukkit.getWorld(value)!!
    }

    override val errorMessage = { sender: Sender, _: String ->
        sender.defaultCommandMessage { command.parsing.world }
    }

    override fun autocomplete(sender: Sender, value: String): List<String> =
        Bukkit.getWorlds()
            .map { it.name }
            .sorted()
}
