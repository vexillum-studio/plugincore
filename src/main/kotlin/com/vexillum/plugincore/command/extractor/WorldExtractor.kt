package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.UUID

open class WorldExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> String,
) : BaseArgumentExtractor<Sender, World>() {

    override val extractor = { _: CommandUser<*>, value: String ->
        Bukkit.getWorld(value) ?: Bukkit.getWorld(UUID.fromString(value))!!
    }

    override fun defaultDescriptor(user: CommandUser<*>): String =
        user.resolve { command.parsing.world }

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): String =
        user.resolve(mapOf("value" to value)) { command.parsing.world }

    override fun autocomplete(sender: Sender, value: String) =
        Bukkit.getWorlds().map { Suggestion<Sender>(it.name) }
}
