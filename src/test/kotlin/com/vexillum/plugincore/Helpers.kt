package com.vexillum.plugincore

import com.vexillum.plugincore.extensions.loadResource
import com.vexillum.plugincore.managers.ManagerFactory
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LocalLanguage
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.managers.language.Message
import org.bukkit.plugin.java.JavaPlugin
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyMap
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private abstract class TestReference : PluginCoreBase()

val pluginCore: PluginCoreBase by lazy {
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

fun message(block: Map<String, Any>.() -> String): Message {
    val messageMock = mock<Message>()
    `when`(messageMock.resolve(anyMap())).thenAnswer { invocation ->
        val inputMap = invocation.getArgument<Map<String, Any>>(0)
        inputMap.block()
    }
    return messageMock
}

inline fun <reified T : Any> languageFromJson(
    languageJson: String,
    localLanguage: LocalLanguage = ENGLISH
) =
    Language.create(
        localLanguage,
        languageJson.byteInputStream(),
        T::class
    )
