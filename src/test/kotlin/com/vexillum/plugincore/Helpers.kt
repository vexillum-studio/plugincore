package com.vexillum.plugincore

import com.vexillum.plugincore.command.CommandName
import com.vexillum.plugincore.command.CommandUsage
import com.vexillum.plugincore.command.SimpleCommand
import com.vexillum.plugincore.extensions.loadResource
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageException
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.managers.ManagerFactory
import org.bukkit.plugin.java.JavaPlugin
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private abstract class TestReference : PluginCoreBase()

val pluginCoreBase: PluginCoreBase by lazy {
    val testDataFolder = TestReference::class.loadResource("data") {
        it.toFile()
    }
    val pluginMock = mock<JavaPlugin> {
        on { name } doReturn ("TestPlugin")
        on { dataFolder } doReturn (testDataFolder)
    }
    mock<TestReference> {
        on { managerFactory } doReturn ManagerFactory(it)
        on { logManager } doReturn mock()
        on { commandManager } doReturn mock()
        on { logManager } doReturn mock()
        on { plugin } doReturn pluginMock
    }
}

internal fun <Sender : LanguageAgent> commandOf(
    pluginCore: PluginCore,
    startToken: String = "/",
    name: CommandName,
    aliases: Set<CommandName> = emptySet(),
    description: ((LanguageAgent) -> String)? = null,
    permission: String? = null,
    usages: List<CommandUsage<Sender>> = emptyList(),
    subCommands: Set<SimpleCommand<Sender>> = emptySet()
): SimpleCommand<Sender> =
    SimpleCommand(
        pluginCore = pluginCore,
        startToken = startToken,
        name = name,
        aliases = aliases,
        description = description,
        permission = permission,
        usages = usages,
        subCommands = subCommands
    )

inline fun <reified T : Any> languageFromJson(
    languageJson: String,
    localLanguage: LocalLanguage = ENGLISH
) =
    Language.create(
        localLanguage,
        languageJson.byteInputStream(),
        T::class
    )

inline fun <reified T : Throwable> assertThrowsMessage(expectedMessage: String, noinline block: () -> Unit): T {
    val throwable = assertThrows(T::class.java, block)
    if (throwable is LanguageException) {
        assertEquals(expectedMessage, throwable.languageMessage.stripped())
    } else {
        assertEquals(expectedMessage, throwable.message)
    }
    return throwable
}
