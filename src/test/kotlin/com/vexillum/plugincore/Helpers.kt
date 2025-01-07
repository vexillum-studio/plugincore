package com.vexillum.plugincore

import com.vexillum.plugincore.extensions.loadResource
import com.vexillum.plugincore.managers.ManagerFactory
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LocalLanguage
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock

private abstract class TestReference : PluginCore

val pluginCore: PluginCore
    get() {
        val testDataFolder = TestReference::class.loadResource("data") {
            it.toFile()
        }
        return mock<TestReference> {
            on { managerFactory } doReturn ManagerFactory(it)
            on { logManager } doReturn mock()
            on { commandManager } doReturn mock()
            on { name } doReturn ("TestPlugin")
            on { logManager } doReturn (mock())
            on { dataFolder } doReturn (testDataFolder)
        }
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
