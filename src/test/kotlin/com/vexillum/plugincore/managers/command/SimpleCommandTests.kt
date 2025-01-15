package com.vexillum.plugincore.managers.command

import com.vexillum.plugincore.command.Command
import com.vexillum.plugincore.command.CommandUsage0
import com.vexillum.plugincore.command.CommandUsage1
import com.vexillum.plugincore.command.CommandUsage2
import com.vexillum.plugincore.command.SimpleCommand
import com.vexillum.plugincore.command.argument.LocationArgument
import com.vexillum.plugincore.command.argument.PlayerArgument
import com.vexillum.plugincore.command.argument.RelativeLocationArgument
import com.vexillum.plugincore.command.session.Session
import com.vexillum.plugincore.managers.language.PluginPlayer
import com.vexillum.plugincore.scenarios.TestServer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class SimpleCommandTests : TestServer() {

    private lateinit var command: SimpleCommand<PluginPlayer>

    private fun scenario1() {
        command = SimpleCommand(
            startToken = Command.SLASH,
            name = "teleport",
            aliases = setOf("tp"),
            description = { "This is a test command" },
            permission = "player.teleport",
            usages = listOf(
                // x y z
                CommandUsage1(RelativeLocationArgument()) { sender, location ->
                    sender.teleport(location)
                },
                // world x y z
                CommandUsage1(LocationArgument()) { sender, location ->
                    sender.teleport(location)
                },
                // player x y z
                CommandUsage2(PlayerArgument({ "player" }), RelativeLocationArgument()) { _, player, location ->
                    player.teleport(location)
                },
                // from to
                CommandUsage2(PlayerArgument({ "from" }), PlayerArgument({ "to" })) { _, from, to ->
                    from.teleport(to)
                }
            ),
            subCommands = emptySet()
        )
    }

    private fun scenario2() {
        command = SimpleCommand(
            startToken = Command.SLASH,
            name = "test",
            aliases = setOf("t"),
            description = { "This is a test command" },
            permission = "player.teleport",
            usages = emptyList(),
            subCommands = setOf(
                SimpleCommand(
                    startToken = Command.SLASH,
                    name = "sub-test",
                    aliases = emptySet(),
                    description = { "This is a test command" },
                    permission = null,
                    usages = listOf(
                        CommandUsage0({})
                    ),
                    subCommands = emptySet()
                ),
                SimpleCommand(
                    startToken = Command.SLASH,
                    name = "other-test",
                    aliases = emptySet(),
                    description = { "This is a test command" },
                    permission = null,
                    usages = listOf(
                        CommandUsage0({})
                    ),
                    subCommands = emptySet()
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
            listOf("world", "world_the_nether", "world_the_end")
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

    private fun session(
        capturedInput: String = "",
        args: Array<String>
    ) =
        Session(
            capturedInput = capturedInput,
            args = args
        )

    private fun assertAutocomplete(
        args: Array<String>,
        expected: List<String>
    ) {
        val session = session(
            args.joinToString(" "),
            args
        )
        assertThat(command.autocomplete(playerMock, session), `is`(expected.toMutableList()))
    }
}
