package com.vexillum.plugincore.scenarios

import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.language.LocaleTranslation
import com.vexillum.plugincore.language.context.State
import com.vexillum.plugincore.language.message.messageFactory
import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.launcher.entities.PluginCorePlayer
import com.vexillum.plugincore.launcher.managers.config.LogConfig
import com.vexillum.plugincore.launcher.managers.config.PluginCoreConfig
import com.vexillum.plugincore.launcher.managers.language.CommandDescriptor
import com.vexillum.plugincore.launcher.managers.language.CommandLanguage
import com.vexillum.plugincore.launcher.managers.language.CommandParsing
import com.vexillum.plugincore.launcher.managers.language.CommandValidation
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.launcher.player.PluginCorePlayerManager
import com.vexillum.plugincore.managers.config.ConfigManager
import com.vexillum.plugincore.pluginCoreBase
import org.bukkit.Bukkit
import org.bukkit.GameMode.CREATIVE
import org.bukkit.GameMode.SURVIVAL
import org.bukkit.Server
import org.bukkit.World
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.MockedStatic
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.clearInvocations
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import java.util.Locale
import java.util.UUID

@Suppress("LongMethod")
open class TestServer {

    private val mockedPlayers = mutableMapOf<UUID, PluginCorePlayer>()
    private val mockedWorlds = mutableMapOf<String, World>()
    protected val pluginCoreLauncher = spy(PluginCoreLauncher()).apply {
        plugin = pluginCoreBase.plugin
    }

    private val serverMock = mock<Server> {
        on { name } doReturn "TestServer"
        on { onlinePlayers } doReturn mockedPlayers.values
    }

    private val bukkit: MockedStatic<Bukkit> = mockStatic(Bukkit::class.java).apply {
        `when`(Bukkit.getServer()) doReturn serverMock
        `when`(Bukkit.getConsoleSender()) doReturn mock()
    }

    val worldMock = mockWorld {
        on { name } doReturn "world"
    }

    val netherMock = mockWorld {
        on { name } doReturn "world_the_nether"
    }

    val endMock = mockWorld {
        on { name } doReturn "world_the_end"
    }

    val adminMock = mockPlayer {
        on { name } doReturn "Admin"
        on { gameMode } doReturn CREATIVE
        on { world } doReturn worldMock
        on { isOp } doReturn true
        on { isFlying } doReturn true
        on { hasPermission(anyString()) } doReturn true
    }

    val playerMock = mockPlayer {
        on { name } doReturn "TestPlayer"
        on { gameMode } doReturn SURVIVAL
        on { world } doReturn worldMock
    }

    @BeforeEach
    fun reset() {
        bukkit.apply {
            `when`(Bukkit.getWorlds()) doReturn mockedWorlds.values.toMutableList()
            `when`(Bukkit.getOnlinePlayers()) doReturn mockedPlayers.values
        }
        for ((name, world) in mockedWorlds) {
            bukkit.apply {
                `when`(Bukkit.getWorld(name)) doReturn world
                `when`(Bukkit.getWorld(world.uid)) doReturn world
            }
        }
        for ((uuid, player) in mockedPlayers) {
            bukkit.apply {
                `when`(Bukkit.getPlayer(uuid)) doReturn player
                `when`(Bukkit.getPlayer(player.name)) doReturn player
            }
        }
        mockLauncher()
    }

    @AfterEach
    fun tearDown() {
        resetCalls()
        bukkit.close()
    }

    fun resetCalls() {
        clearInvocations(*mockedWorlds.values.toTypedArray())
        clearInvocations(*mockedPlayers.values.toTypedArray())
        clearInvocations(pluginCoreLauncher)
        clearInvocations(serverMock)
        bukkit.clearInvocations()
    }

    private fun mockWorld(
        stubbing: KStubbing<World>.() -> Unit
    ): World {
        val uuid = UUID.randomUUID()
        val world = mock<World> {
            on { uid } doReturn uuid
            stubbing.invoke(this)
        }
        mockedWorlds[world.name] = world
        return world
    }

    private fun mockPlayer(
        stubbing: KStubbing<PluginPlayer>.() -> Unit
    ): PluginPlayer {
        val uuid = UUID.randomUUID()
        val agent = mock<PluginCorePlayer> {
            on { localLanguage } doReturn ENGLISH
        }
        val pluginPlayer = mock<PluginCorePlayer> {
            on { uniqueId } doReturn uuid
            on { locale } doReturn Locale.ENGLISH.toString()
            on { localLanguage } doReturn ENGLISH
            on { languageState(pluginCoreLauncher) } doAnswer {
                State(agent, pluginCoreLauncher.translation(ENGLISH).value)
            }
            stubbing.invoke(this)
        }

        mockedPlayers[uuid] = pluginPlayer
        return pluginPlayer
    }

    private fun mockLauncher() {
        val pluginCoreLanguage = messageFactory {
            PluginCoreLanguage(
                prefix = messageOf("[", param("pluginName"), "]: "),
                color = messageOf("&7"),
                errorColor = messageOf("&c"),
                errorAccent = messageOf("&4"),
                command = CommandLanguage(
                    helpMessage = messageOf("Help for command ", param("label"), ":"),
                    unknownUsage = messageOf("Unknown usage, instead use:"),
                    incorrectUsage = messageOf("Incorrect usage for argument ", param("argument"), ":"),
                    permissionMessage = messageOf("You don't have permission to execute that command"),
                    transformMessage = messageOf("You can't transform '", param("value"), "' to '", param("to"), "'"),
                    parsing = CommandParsing(
                        boolean = messageOf("'", param("value"), "' can't be parsed as a boolean value"),
                        integer = messageOf("'", param("value"), "' can't be parsed as an integer value"),
                        double = messageOf("'", param("value"), "' can't be parsed as a numeric value"),
                        enum = messageOf(
                            "'",
                            param("value"),
                            "' must be one of the next values: ",
                            param("possibleValues")
                        ),
                        player = messageOf("Player '", param("name"), "' was not found"),
                        world = messageOf("The world '", param("value"), "' was not found"),
                        vector = messageOf(
                            "Invalid definition, use valid numbers for: <",
                            param("x"),
                            "> <",
                            param("y"),
                            "> <",
                            param("z"),
                            ">"
                        ),
                        location = messageOf(
                            "Invalid location, use: <",
                            param("x"),
                            "> <",
                            param("y"),
                            "> <",
                            param("z"),
                            ">"
                        )
                    ),
                    validation = CommandValidation(
                        numberRange = messageOf(
                            "The value ",
                            param("value"),
                            " must be between ",
                            param("min"),
                            " and ",
                            param("max")
                        )
                    ),
                    descriptor = CommandDescriptor(
                        color = messageOf("&7"),
                        accent = messageOf("&c"),
                        prefix = messageOf("&c<"),
                        postfix = messageOf("&c>"),
                        marker = messageOf("└▶"),
                        world = messageOf("world"),
                        x = messageOf("x"),
                        y = messageOf("y"),
                        z = messageOf("z")
                    )
                )
            )
        }

        val localeTranslation = mock<LocaleTranslation<PluginCoreLanguage>> {
            on { value } doReturn pluginCoreLanguage
        }

        val configManager = mock<ConfigManager<PluginCoreConfig>> {
            on { invoke() } doReturn PluginCoreConfig(
                monospacedFont = true,
                logs = LogConfig(
                    folder = "logs",
                    prefixFormat = "[hh:mm:ss.SSS]",
                    fileFormat = "yyyy-MM-dd"
                )
            )
        }

        val playerManager = mock<PluginCorePlayerManager> {
            for ((uuid, player) in mockedPlayers) {
                on { getOrCreatePluginCorePlayer(player) } doReturn player
                on { getOrCreatePluginCorePlayer(uuid) } doReturn player
            }
        }

        doReturn(configManager).whenever(pluginCoreLauncher).configManager
        doReturn(localeTranslation).whenever(pluginCoreLauncher).translation(ENGLISH)
        doReturn(playerManager).whenever(pluginCoreLauncher).playerManager

        PluginCoreLauncher.pluginCoreInstance = pluginCoreLauncher
    }
}
