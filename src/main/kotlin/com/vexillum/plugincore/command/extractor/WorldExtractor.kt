package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.launcher.defaultCommandMessage
import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.UUID

open class WorldExtractor<Sender : LanguageAgent>(
    override val descriptor: (LanguageAgent) -> String
) : BaseArgumentExtractor<Sender, World>() {

    override val extractor = { _: Sender, value: String ->
        Bukkit.getWorld(value) ?: Bukkit.getWorld(UUID.fromString(value))!!
    }

    override val errorMessage = { sender: Sender, value: String ->
        sender.defaultCommandMessage(mapOf("value" to value)) { command.parsing.world }
    }

    override fun autocomplete(sender: Sender, value: String) =
        Bukkit.getWorlds().map { Suggestion<Sender>(it.name) }
}
