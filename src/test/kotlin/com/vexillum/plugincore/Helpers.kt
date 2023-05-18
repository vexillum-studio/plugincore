package com.vexillum.plugincore

import com.vexillum.plugincore.managers.ManagerFactory
import com.vexillum.plugincore.managers.language.Language
import com.vexillum.plugincore.managers.language.LocalLanguage
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import java.io.File

fun managerFactory(
    dataFolderFile: File,
    pluginName: String = "TestPlugin"
): ManagerFactory {
    val pluginCore = mock<PluginCore> {
        on { name } doReturn (pluginName)
        on { logManager } doReturn (mock())
        on { dataFolder } doReturn (dataFolderFile)
    }
    return ManagerFactory(pluginCore)
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
