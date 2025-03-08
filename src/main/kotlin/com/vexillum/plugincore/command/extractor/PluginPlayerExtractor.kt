package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.message
import org.bukkit.Bukkit
import org.bukkit.entity.Player

open class PluginPlayerExtractor<Sender : LanguageAgent, P : PluginPlayer>(
    override val descriptor: (CommandUser<*>) -> LanguageMessage,
    val block: (Player) -> P?
) : BaseArgumentExtractor<Sender, P>() {

    override val extractor = { _: CommandUser<Sender>, name: String ->
        block(Bukkit.getPlayer(name)!!)!!
    }

    override fun defaultDescriptor(user: CommandUser<*>): LanguageMessage =
        descriptor(user)

    override fun defaultErrorMessage(user: CommandUser<*>, value: String): LanguageMessage =
        user.resolve(mapOf("name" to value)) { command.parsing.player }

    override fun autocomplete(sender: Sender, value: String) =
        Bukkit
            .getOnlinePlayers()
            .map { Suggestion<Sender>(message(it.name)) }

    override fun matchingScore(sender: Sender, value: String): Double =
        if (usernameRegex.matches(value)) {
            super.matchingScore(sender, value)
        } else 0.0

    companion object {
        private val usernameRegex = Regex("^[a-zA-Z._][a-zA-Z0-9_]{2,20}$")
    }
}
