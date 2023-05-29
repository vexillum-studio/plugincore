package com.vexillum.plugincore.launcher

import com.vexillum.plugincore.PluginCoreBase
import com.vexillum.plugincore.command.argument.LocationArgument
import com.vexillum.plugincore.command.argument.PlayerArgument
import com.vexillum.plugincore.command.argument.RelativeLocationArgument
import com.vexillum.plugincore.extensions.disablePlugin
import com.vexillum.plugincore.launcher.managers.config.PluginCoreConfig
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LanguageContext
import com.vexillum.plugincore.managers.language.LocalLanguage

fun main(args: Array<String>) {
    println(args)
}

class PluginCoreLauncher : PluginCoreBase(), LanguageContext<PluginCoreLanguage> {

    init {
        instance = this
    }

    val configManager = managerFactory.newConfigManager(
        PluginCoreConfig::class,
        "plugincore",
        "config"
    )

    private val languageManager = managerFactory.newLanguageManager(
        PluginCoreLanguage::class,
        "language",
        "language"
    )

    override fun enable() {

        logManager.info("PluginCore started")

        commandManager.registerPlayerCommand {

            name = "warp"
            addAliases("warpy")
            permission = "player.teleport"
            description {
                "teleports to somewhere"
            }

            addUsage {
                it.sendMessage("empty")
            }

            addUsage {
                it.sendMessage("empty override")
            }

            addUsage(
                PlayerArgument({ "player" })
            ) { player, other ->
                player.teleport(other)
            }

            addUsage(
                RelativeLocationArgument()
            ) { player, location ->
                player.teleport(location)
                player.sendMessage("Teleported to ${location.x}, ${location.y}, ${location.z}")
            }

            addUsage(
                LocationArgument()
            ) { player, location ->
                player.teleport(location)
                player.sendMessage("Teleported to ${location.world} ${location.x}, ${location.y}, ${location.z}")
            }

            addSubCommand {
                name = "test"
                addUsage {
                    it.sendMessage(instance, mapOf("value" to "peras", "type" to "manzanas")) { command.transformMessage }
                }
            }
        }
    }

    override fun afterEnable() {
        if (languageManager.loadedLanguages.isEmpty()) {
            logManager.error("PluginCore has been disabled due to not being able to load the default languages")
            disablePlugin()
        }
    }

    override fun disable() {
    }

    companion object {
        lateinit var instance: PluginCoreLauncher
            private set
    }

    override fun language(localLanguage: LocalLanguage): Language<PluginCoreLanguage> =
        languageManager.language(localLanguage)
}
