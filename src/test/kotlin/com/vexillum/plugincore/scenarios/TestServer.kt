package com.vexillum.plugincore.scenarios

import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.launcher.managers.language.CommandDescriptor
import com.vexillum.plugincore.launcher.managers.language.CommandLanguage
import com.vexillum.plugincore.launcher.managers.language.CommandParsing
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.managers.language.PluginPlayer
import com.vexillum.plugincore.message
import org.bukkit.Bukkit
import org.bukkit.Bukkit.getConsoleSender
import org.bukkit.Bukkit.getOnlinePlayers
import org.bukkit.Bukkit.getPlayer
import org.bukkit.Bukkit.getServer
import org.bukkit.Bukkit.getWorlds
import org.bukkit.GameMode.CREATIVE
import org.bukkit.GameMode.SURVIVAL
import org.bukkit.Server
import org.bukkit.World
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mockStatic
import org.mockito.kotlin.KStubbing
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.mockito.kotlin.spy
import org.mockito.kotlin.whenever
import java.util.Locale
import java.util.UUID

open class TestServer {

    private val mockedPlayers = mutableListOf<PluginPlayer>()
    private val mockedWorlds = mutableListOf<World>()

    private val bukkit = mockStatic(Bukkit::class.java)

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
    }

    val playerMock = mockPlayer {
        on { name } doReturn "TestPlayer"
        on { gameMode } doReturn SURVIVAL
        on { world } doReturn worldMock
    }

    val serverMock = mock<Server> {
        on { name } doReturn "TestServer"
        on { onlinePlayers } doReturn mockedPlayers
    }

    @BeforeEach
    fun reset() {
        bukkit.apply {
            `when`(getServer()) doReturn serverMock
            `when`(getConsoleSender()) doReturn mock()
            `when`(getWorlds()) doReturn mockedWorlds
            `when`(getOnlinePlayers()) doReturn mockedPlayers
        }
        mockLauncher()
    }

    @AfterEach
    fun tearDown() {
        bukkit.close()
    }

    fun mockWorld(
        stubbing: KStubbing<World>.() -> Unit
    ): World {
        val world = mock<World> {
            on { uid } doReturn UUID.randomUUID()
            stubbing.invoke(this)
        }
        bukkit.apply {
            `when`(Bukkit.getWorld(world.name)) doReturn world
            `when`(Bukkit.getWorld(world.uid)) doReturn world
        }
        mockedWorlds.add(world)
        return world
    }

    fun mockPlayer(
        stubbing: KStubbing<PluginPlayer>.() -> Unit
    ): PluginPlayer {
        val pluginPlayer = mock<PluginPlayer> {
            on { uniqueId } doReturn UUID.randomUUID()
            on { locale } doReturn Locale.ENGLISH.toString()
            on { localLanguage } doReturn ENGLISH
            stubbing.invoke(this)
        }
        bukkit.apply {
            `when`(getPlayer(pluginPlayer.uniqueId)) doReturn pluginPlayer
            `when`(getPlayer(pluginPlayer.name)) doReturn pluginPlayer
        }
        mockedPlayers.add(pluginPlayer)
        return pluginPlayer
    }

    fun mockLauncher() {
        val pluginCoreLanguage = PluginCoreLanguage(
            command = CommandLanguage(
                prefix = message { "" },
                errorColor = message { "" },
                errorAccent = message { "" },
                permissionMessage = message { "" },
                transformMessage = message { "" },
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

        val pluginCoreLauncher = spy(PluginCoreLauncher())

        doReturn(language).whenever(pluginCoreLauncher).language(ENGLISH)

        PluginCoreLauncher.pluginCoreInstance = pluginCoreLauncher
    }
}
