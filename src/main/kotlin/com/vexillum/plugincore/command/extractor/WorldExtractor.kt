package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.UUID

open class WorldExtractor<Sender : LanguageAgent>(
    override val descriptor: (CommandUser<*>) -> Message,
) : BaseArgumentExtractor<Sender, World>() {

    override val extractor = { _: CommandUser<*>, value: String ->
        Bukkit.getWorld(value) ?: Bukkit.getWorld(UUID.fromString(value))!!
    }

    override fun defaultDescriptor(user: CommandUser<*>): Message =
        user.resolve { command.parsing.world }

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): Message =
        user.resolve { command.parsing.world }.replace("value", value)

    override fun autocomplete(sender: Sender, value: String) =
        Bukkit.getWorlds().map { Suggestion<Sender>(message(it.name)) }
}
