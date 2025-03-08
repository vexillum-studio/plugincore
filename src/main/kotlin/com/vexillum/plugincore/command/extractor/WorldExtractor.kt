package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.UUID

open class WorldExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> LanguageMessage,
) : BaseArgumentExtractor<Sender, World>() {

    override val extractor = { _: CommandUser<*>, value: String ->
        Bukkit.getWorld(value) ?: Bukkit.getWorld(UUID.fromString(value))!!
    }

    override fun defaultDescriptor(user: CommandUser<*>): LanguageMessage =
        user.resolve { command.parsing.world }

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): LanguageMessage =
        user.resolve(mapOf("value" to value)) { command.parsing.world }

    override fun autocomplete(sender: Sender, value: String) =
        Bukkit.getWorlds().map { Suggestion<Sender>(message(it.name)) }
}
