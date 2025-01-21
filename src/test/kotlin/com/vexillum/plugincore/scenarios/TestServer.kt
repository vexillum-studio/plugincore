package com.vexillum.plugincore.scenarios

import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.launcher.entities.PluginCorePlayer
import com.vexillum.plugincore.launcher.managers.language.CommandDescriptor
import com.vexillum.plugincore.launcher.managers.language.CommandLanguage
import com.vexillum.plugincore.launcher.managers.language.CommandParsing
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.launcher.player.PluginCorePlayerManager
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.message
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
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import java.util.Locale
import java.util.UUID

open class TestServer {

    private val mockedPlayers = mutableMapOf<UUID, PluginCorePlayer>()
    private val mockedWorlds = mutableMapOf<String, World>()

    private lateinit var bukkit: MockedStatic<Bukkit>
    private lateinit var pluginCoreLauncher: PluginCoreLauncher

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

    val serverMock = mock<Server> {
        on { name } doReturn "TestServer"
        on { onlinePlayers } doReturn mockedPlayers.values
    }

    @BeforeEach
    fun reset() {
        bukkit = mockStatic(Bukkit::class.java)
        bukkit.apply {
            `when`(Bukkit.getServer()) doReturn serverMock
            `when`(Bukkit.getConsoleSender()) doReturn mock()
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
        val pluginPlayer = mock<PluginCorePlayer> {
            on { uniqueId } doReturn uuid
            on { locale } doReturn Locale.ENGLISH.toString()
            on { localLanguage } doReturn ENGLISH
            stubbing.invoke(this)
        }
        mockedPlayers[uuid] = pluginPlayer
        return pluginPlayer
    }

    private fun mockLauncher() {
        val pluginCoreLanguage = PluginCoreLanguage(
            command = CommandLanguage(
                prefix = message { "" },
                errorColor = message { "" },
                errorAccent = message { "" },
                permissionMessage = message { "You don't have permission to execute that command" },
                transformMessage = message { "You can't transform '${get("value")}' to '${get("to")}'" },
                parsing = CommandParsing(
                    boolean = message { "'${get("value")}' can't be parsed as a boolean value" },
                    integer = message { "'${get("value")}' can't be parsed as an integer value" },
                    double = message { "'${get("value")}' can't be parsed as a double value" },
                    enum = message { "'${get("value")}' must be one of the next values: ${get("possibleValues")}" },
                    player = message { "Player '${get("name")}' was not found" },
                    world = message { "The world '${get("value")}' was not found" },
                    vector = message { "Invalid definition, use valid numbers for: <${get("x")}> <${get("y")}> <${get("z")}>" },
                    location = message { "Invalid location, use: <${get("x")}> <${get("y")}> <${get("z")}>" }
                ),
                descriptor = CommandDescriptor(
                    color = message { "" },
                    accent = message { "" },
                    prefix = message { "<" },
                    postfix = message { ">" },
                    world = message { "world" },
                    x = message { "x" },
                    y = message { "y" },
                    z = message { "z" }
                )
            )
        )

        val language = mock<Language<PluginCoreLanguage>> {
            on { language } doReturn pluginCoreLanguage
        }

        val playerManager = mock<PluginCorePlayerManager> {
            for ((uuid, player) in mockedPlayers) {
                on { getOrCreatePluginCorePlayer(player) } doReturn player
                on { getOrCreatePluginCorePlayer(uuid) } doReturn player
            }
        }

        pluginCoreLauncher = spy(PluginCoreLauncher())

        doReturn(language).whenever(pluginCoreLauncher).language(ENGLISH)
        doReturn(playerManager).whenever(pluginCoreLauncher).playerManager

        PluginCoreLauncher.pluginCoreInstance = pluginCoreLauncher
    }
}
