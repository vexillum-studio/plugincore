package com.vexillum.plugincore.managers.command

import com.vexillum.plugincore.assertThrowsMessage
import com.vexillum.plugincore.command.CommandException
import com.vexillum.plugincore.command.CommandUsage0
import com.vexillum.plugincore.command.CommandUsage1
import com.vexillum.plugincore.command.CommandUsage2
import com.vexillum.plugincore.command.SimpleCommand
import com.vexillum.plugincore.command.argument.LocationArgument
import com.vexillum.plugincore.command.argument.PlayerArgument
import com.vexillum.plugincore.command.argument.SenderLocationArgument
import com.vexillum.plugincore.command.session.Session
import com.vexillum.plugincore.command.session.User
import com.vexillum.plugincore.commandOf
import com.vexillum.plugincore.entities.PluginPlayer
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.scenarios.TestServer
import org.bukkit.Location
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

@Suppress("LongMethod")
class SimpleCommandTests : TestServer() {

    private lateinit var command: SimpleCommand<PluginPlayer>

    private fun scenario1() {
        command = commandOf(
            pluginCore = pluginCoreLauncher,
            name = "teleport",
            aliases = setOf("tp"),
            description = { "This is a test command" },
            permission = "player.teleport",
            usages = listOf(
                // x y z
                CommandUsage1(SenderLocationArgument()) { sender, location ->
                    sender.teleport(location)
                },
                // world x y z
                CommandUsage1(LocationArgument()) { sender, location ->
                    sender.teleport(location)
                },
                // player x y z
                CommandUsage2(PlayerArgument("player"), SenderLocationArgument()) { _, player, location ->
                    player.teleport(location)
                },
                // from to
                CommandUsage2(PlayerArgument("from"), PlayerArgument("to")) { _, from, to ->
                    from.teleport(to)
                }
            )
        )
    }

    private fun scenario2() {
        command = commandOf(
            pluginCoreLauncher,
            name = "test",
            aliases = setOf("t"),
            description = { "This is a test command" },
            permission = "player.teleport",
            usages = emptyList(),
            subCommands = setOf(
                commandOf(
                    pluginCoreLauncher,
                    name = "sub-test",
                    description = { "This is a test command" },
                    usages = listOf(
                        CommandUsage0 {}
                    )
                ),
                commandOf(
                    pluginCoreLauncher,
                    name = "other-test",
                    description = { "This is a test command" },
                    usages = listOf(
                        CommandUsage0 {}
                    )
                )
            )
        )
    }

    @Test
    fun `should match correctly by name ignoring case`() {
        scenario1()
        assertThat(command.matches("Teleport"), `is`(true))
    }

    @Test
    @Suppress("SpellCheckingInspection")
    fun `should not match by name`() {
        scenario1()
        assertThat(command.matches("telepor"), `is`(false))
    }

    @Test
    fun `should match correctly by alias ignoring case`() {
        scenario1()
        assertThat(command.matches("Tp"), `is`(true))
    }

    @Test
    fun `should not match by alias`() {
        scenario1()
        assertThat(command.matches("t"), `is`(false))
    }

    @Test
    fun `should get correct sub-command autocompletes`() {
        scenario2()
        assertAutocomplete(
            arrayOf(""),
            listOf("sub-test", "other-test")
        )
        assertAutocomplete(
            arrayOf("t"),
            listOf("sub-test", "other-test")
        )
        assertAutocomplete(
            arrayOf("sub"),
            listOf("sub-test")
        )
        assertAutocomplete(
            arrayOf("ot"),
            listOf("other-test")
        )
        assertAutocomplete(
            arrayOf("sub-test"),
            listOf()
        )
    }

    @Test
    @Suppress("LongMethod")
    fun `should get correct usage autocompletes`() {
        scenario1()

        // All usages must be suggested
        assertAutocomplete(
            arrayOf(""),
            listOf("<x>", "<world>", "<player>", "<from>")
        )

        // x y z
        assertAutocomplete(
            arrayOf("100"),
            listOf("<x>")
        )
        assertAutocomplete(
            arrayOf("100", "200"),
            listOf("<y>")
        )
        assertAutocomplete(
            arrayOf("100", "200", "300"),
            listOf("<z>")
        )

        // world x y z
        assertAutocomplete(
            arrayOf("w"),
            listOf("world", "world_the_end", "world_the_nether")
        )
        assertAutocomplete(
            arrayOf("world"),
            listOf("world_the_end", "world_the_nether")
        )
        assertAutocomplete(
            arrayOf("nether"),
            listOf("world_the_nether")
        )

        assertAutocomplete(
            arrayOf("world", "100"),
            listOf("<x>")
        )
        assertAutocomplete(
            arrayOf("world", "100", "200"),
            listOf("<y>")
        )
        assertAutocomplete(
            arrayOf("world", "100", "200", "300"),
            listOf("<z>")
        )

        // player x y z
        assertAutocomplete(
            arrayOf("TestPlayer"),
            listOf()
        )
        assertAutocomplete(
            arrayOf("TestPlayer", ""),
            listOf("<x>", "<to>")
        )
        assertAutocomplete(
            arrayOf("TestPlayer", "100"),
            listOf("<x>")
        )
        assertAutocomplete(
            arrayOf("TestPlayer", "100", ""),
            listOf("<y>")
        )
        assertAutocomplete(
            arrayOf("TestPlayer", "100", "200"),
            listOf("<y>")
        )
        assertAutocomplete(
            arrayOf("TestPlayer", "100", "200", ""),
            listOf("<z>")
        )
        assertAutocomplete(
            arrayOf("TestPlayer", "100", "200", "300"),
            listOf("<z>")
        )
        assertAutocomplete(
            arrayOf("TestPlayer", "100", "200", "3ABC"),
            listOf("'3ABC' can't be parsed as a numeric value")
        )

        // from to
        assertAutocomplete(
            arrayOf("a"),
            listOf("Admin", "TestPlayer")
        )
        assertAutocomplete(
            arrayOf("Admin", "p"),
            listOf("TestPlayer")
        )
        assertAutocomplete(
            arrayOf("Admin", "TestPlayer"),
            listOf()
        )
    }

    @Test
    fun `the localLanguage property should be called just once for all the command process`() {
        scenario1()
        // A singe LanguageState is created at the start of the process
        // all messages should resolve from there in the CommandSession without asking again for the player's language
        execute(adminMock, "Admin", "TestPlayer")
        verify(adminMock, times(1)).localLanguage
    }

    @Test
    fun `should throw CommandException when the player doesnt have permission`() {
        scenario1()
        // playerMock doesn't have permission to execute this command
        assertThrowsMessage<CommandException>("[TestPlugin]: You don't have permission to execute that command") {
            execute(playerMock, "100", "200", "300")
        }
    }

    @Test
    fun `should throw error on invalid usages`() {
        scenario1()
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Unknown usage, instead use:
            /teleport <x> <y> <z>
            /teleport <world> <x> <y> <z>
            /teleport <player> <x> <y> <z>
            /teleport <from> <to>
            """.trimIndent()
        ) {
            execute(adminMock, "")
        }
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Unknown usage, instead use:
            /teleport <x> <y> <z>
            /teleport <world> <x> <y> <z>
            /teleport <player> <x> <y> <z>
            /teleport <from> <to>
            """.trimIndent()
        ) {
            execute(adminMock, "world", "100", "200", "300", "400")
        }
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Incorrect usage for argument y:
            /teleport <x> <y> <z>
                           └▶ 'y-coordinate' can't be parsed as a numeric value
            """.trimIndent()
        ) {
            execute(adminMock, "100", "y-coordinate")
        }
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Incorrect usage for argument z:
            /teleport <x> <y> <z>
                               └▶ 'z-coordinate' can't be parsed as a numeric value
            """.trimIndent()
        ) {
            execute(adminMock, "100", "200", "z-coordinate")
        }
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Incorrect usage for argument world:
            /teleport <world> <x> <y> <z>
                       └▶ The world 'wor' was not found
            """.trimIndent()
        ) {
            execute(adminMock, "wor")
        }
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Incorrect usage for argument world:
            /teleport <world> <x> <y> <z>
                       └▶ The world 'end' was not found
            """.trimIndent()
        ) {
            execute(adminMock, "end")
        }
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Incorrect usage for argument player:
            /teleport <player>
                       └▶ Player 'Adm' was not found
            """.trimIndent()
        ) {
            execute(adminMock, "Adm")
        }
        assertThrowsMessage<CommandException>(
            """
            [TestPlugin]: Incorrect usage for argument x:
            /teleport <world> <x> <y> <z>
                               └▶ '#' can't be parsed as a numeric value
            """.trimIndent()
        ) {
            execute(adminMock, "world", "#")
        }
    }

    @Test
    fun `should execute command usages correctly`() {
        scenario1()
        execute(adminMock, "100", "200", "300")
        verify(adminMock, times(1)).teleport(Location(worldMock, 100.0, 200.0, 300.0))
        resetCalls()

        execute(adminMock, "world_the_nether", "100", "200", "300")
        verify(adminMock, times(1)).teleport(Location(netherMock, 100.0, 200.0, 300.0))
        resetCalls()

        execute(adminMock, "TestPlayer", "100", "200", "300")
        verify(playerMock, times(1)).teleport(Location(worldMock, 100.0, 200.0, 300.0))
        resetCalls()

        execute(adminMock, "Admin", "TestPlayer")
        verify(adminMock, times(1)).teleport(playerMock)
    }

    private fun assertAutocomplete(
        args: Array<String>,
        expected: List<String>,
        sender: PluginPlayer = adminMock
    ) {
        val session = session(sender, args)
        assertThat(command.autocomplete(session), `is`(expected.toMutableList()))
    }

    private fun execute(
        sender: PluginPlayer,
        vararg args: String
    ) {
        command.execute(session(sender, arrayOf(*args)))
    }

    private fun <Sender : LanguageAgent> session(
        sender: Sender,
        args: Array<String>,
        capturedInput: String = args.joinToString(" ")
    ) =
        Session(
            user = User(sender),
            capturedInput = capturedInput,
            args = args
        )
}
