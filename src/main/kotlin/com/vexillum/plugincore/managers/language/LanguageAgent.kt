package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.launcher.entities.PluginCorePlayer
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.managers.language.context.LanguageContext
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.OfflinePlayer
import org.bukkit.Sound
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.conversations.Conversable
import org.bukkit.entity.Player
import java.util.UUID

interface LanguageAgent : CommandSender, Conversable {

    val activeLanguage: LocalLanguage?

    val localLanguage get() = activeLanguage ?: LocalLanguage.DEFAULT

    fun <T : Any> sendMessage(
        context: LanguageContext<T>,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ) {
        val resolved = context.language(localLanguage).resolve(replacements, block)
        sendMessage(resolved)
    }

    fun <T : Any> commandException(
        languageContext: LanguageContext<T>,
        replacements: Map<String, Any> = emptyMap(),
        block: T.() -> Message
    ): Nothing {
        languageContext.commandException(this, replacements, block)
    }
}

private val consoleSender = Bukkit.getConsoleSender()

object Console : LanguageAgent, ConsoleCommandSender by consoleSender {
    override val activeLanguage: LocalLanguage = ENGLISH
}

object BukkitConsole : LanguageAgent, ConsoleCommandSender by consoleSender {

    override val activeLanguage: LocalLanguage
        get() = ENGLISH // TODO Change to config on plugincore launcher
}

abstract class OfflinePluginPlayer private constructor(
    private val offlinePlayer: OfflinePlayer
) : LanguageAgent, OfflinePlayer by offlinePlayer {

    constructor(uuid: UUID) : this(Bukkit.getOfflinePlayer(uuid))

    override fun getName(): String =
        offlinePlayer.name ?: uniqueId.toString()

    @Transient
    override val activeLanguage: LocalLanguage? =
        PluginCorePlayer.of(uniqueId).persistedLanguage.let { LocalLanguage.ofCode(it) }

    override fun hashCode(): Int =
        uniqueId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        return uniqueId == (other as PluginPlayer).uniqueId
    }
}

abstract class PluginPlayer internal constructor(
    private val player: Player
) : LanguageAgent, Player by player {

    constructor(uuid: UUID) :
        this(Bukkit.getPlayer(uuid) ?: error("The player with uuid $uuid is offline"))

    final override val activeLanguage: LocalLanguage?
        get() = LocalLanguage.ofCode(player.locale)

    fun <T : Any> commandException(
        languageContext: LanguageContext<T>,
        replacements: Map<String, Any> = emptyMap(),
        sound: Sound? = null,
        block: T.() -> Message
    ): Nothing {
        sound?.let { playSound(sound) }
        languageContext.commandException(this, replacements, block)
    }

    fun playSound(
        sound: Sound,
        location: Location = this.location,
        pitch: Float = 1F,
        volume: Float = 1F
    ) {
        playSound(location, sound, volume, pitch)
    }

    override fun hashCode(): Int =
        uniqueId.hashCode()

    override fun equals(other: Any?): Boolean {
        if (javaClass != other?.javaClass) return false
        return uniqueId == (other as PluginPlayer).uniqueId
    }
}

fun Player.pluginPlayer(): PluginPlayer =
    PluginCorePlayer.of(uniqueId)
