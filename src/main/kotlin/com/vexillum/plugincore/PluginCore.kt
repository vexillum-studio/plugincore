@file:Suppress("EmptyFunctionBlock")

package com.vexillum.plugincore

import com.vexillum.plugincore.extensions.implementationVersion
import com.vexillum.plugincore.extensions.loadResourceAsStream
import com.vexillum.plugincore.extensions.loadResourceAsString
import com.vexillum.plugincore.extensions.registerEvents
import com.vexillum.plugincore.extensions.runOnNextTick
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.LocaleTranslation
import com.vexillum.plugincore.language.context.LanguageContext
import com.vexillum.plugincore.language.deserializer.MessageDeserializer
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.ManagerFactory
import com.vexillum.plugincore.managers.command.CommandManager
import com.vexillum.plugincore.managers.language.DefaultLanguageResolver
import com.vexillum.plugincore.managers.language.DefaultLanguageResolverImpl
import com.vexillum.plugincore.managers.log.LogManager
import com.vexillum.plugincore.stdout.Ansi
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.PluginEnableEvent
import org.bukkit.plugin.java.JavaPlugin
import java.time.LocalDateTime
import kotlin.random.Random

interface PluginCore : LanguageContext<PluginCoreLanguage> {
    val plugin: JavaPlugin
    val managerFactory: ManagerFactory
    val logManager: LogManager
    val commandManager: CommandManager
    fun enable()
    fun disable()
    fun afterEnable()
}

abstract class PluginCoreBase : PluginCore, DefaultLanguageResolver, Listener {

    final override val pluginCore = this

    override lateinit var plugin: JavaPlugin
        internal set
    final override val managerFactory: ManagerFactory by lazy {
        ManagerFactory(this)
    }
    final override val logManager: LogManager by lazy {
        managerFactory.newLogManager()
    }
    final override val commandManager: CommandManager by lazy {
        managerFactory.commandManager()
    }

    override fun enable() {}

    override fun disable() {}

    override fun afterEnable() {}

    override val defaultLanguageContext: LanguageContext<PluginCoreLanguage> by
    DefaultLanguageResolverImpl(managerFactory)

    final override fun translation(
        localLanguage: LocalLanguage
    ): LocaleTranslation<PluginCoreLanguage> =
        defaultLanguageContext.translation(localLanguage)
}

abstract class PluginCoreBoot(
    val pluginCore: PluginCoreBase
) : JavaPlugin() {

    final override fun onEnable() {
        pluginCore.plugin = this
        printBanner()
        registerEvents(pluginCore)
        pluginCore.managerFactory.start()
        pluginCore.enable()
    }

    final override fun onDisable() {
        pluginCore.disable()
        pluginCore.managerFactory.stop()
    }

    private fun printBanner() {
        this::class.loadResourceAsString(DEFAULT_BANNER_PATH)?.let { bannerString ->
            val bannerMessage = MessageDeserializer.deserialize(bannerString)
                .replace("pluginName", pluginCore.plugin.name)

            pluginCore.plugin.implementationVersion?.let { pluginVersion ->
                bannerMessage.replace("pluginVersion", pluginVersion)
            }

            pluginCoreInstance.plugin.implementationVersion?.let { pluginCoreVersion ->
                bannerMessage.replace("pluginCoreVersion", pluginCoreVersion)
            }

            val config = pluginCoreInstance.configManager()
            val currentDateTime = LocalDateTime.now()
            bannerMessage.replace("date", currentDateTime.format(config.humanDateFormat))
            bannerMessage.replace("time", currentDateTime.format(config.timeFormat))

            getMessageOfTheDay()?.let { messageOfTheDay ->
                bannerMessage.replace("messageOfTheDay", messageOfTheDay)
            }

            bannerMessage.replacingWith(Ansi.replacements)
            println(bannerMessage)
        }
    }

    private fun getMessageOfTheDay(): String? {
        try {
            val resourceStream = this::class.loadResourceAsStream(DEFAULT_MOTD_PATH) ?: return null
            // Reservoir sampling
            resourceStream.bufferedReader().useLines { lines ->
                var selectedLine: String? = null
                var count = 0
                for (line in lines) {
                    count++
                    if (Random.nextInt(count) == 0) {
                        selectedLine = line
                    }
                }
                return selectedLine
            }
        } catch (e: Exception) {
            return null
        }
    }

    @EventHandler
    fun on(event: PluginEnableEvent) {
        if (event.plugin === this) {
            runOnNextTick(pluginCore::afterEnable)
        }
    }

    companion object {
        private const val DEFAULT_BANNER_PATH = "banner.txt"
        private const val DEFAULT_MOTD_PATH = "motd.txt"
    }
}
