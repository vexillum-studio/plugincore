package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.PluginCoreBase
import com.vexillum.plugincore.extensions.loadResourceAsURL
import com.vexillum.plugincore.language.context.LanguageContext
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.managers.ManagerFactory
import java.io.File
import kotlin.reflect.KProperty

internal interface DefaultLanguageResolver {
    val defaultLanguageContext: LanguageContext<PluginCoreLanguage>
}

internal class DefaultLanguageResolverImpl(
    private val managerFactory: ManagerFactory
) : DefaultLanguageResolver {

    operator fun getValue(
        pluginCoreBase: PluginCoreBase,
        property: KProperty<*>
    ): LanguageContext<PluginCoreLanguage> =
        defaultLanguageContext

    override val defaultLanguageContext: LanguageContext<PluginCoreLanguage> by lazy {
        val pluginCore = managerFactory.pluginCore
        try {
            val defaultOverrideURL = pluginCore::class.loadResourceAsURL(DEFAULT_LANGUAGE_PATH)
                ?: File(pluginCore.plugin.dataFolder, DEFAULT_LANGUAGE_PATH).toURI().toURL()
                ?: error("Default language not found")
            pluginCore.logManager.info("Overriding default language from ${defaultOverrideURL.path}")
            managerFactory.newLanguageManager(
                languageClass = PluginCoreLanguage::class,
                originFolderPath = DEFAULT_LANGUAGE_PATH,
                destinationFolderPath = DEFAULT_LANGUAGE_PATH
            ).also { it.reload() }
        } catch (exception: Exception) {
            pluginCoreInstance
        }
    }

    companion object {
        private const val DEFAULT_LANGUAGE_PATH = "defaultLanguage"
    }
}
