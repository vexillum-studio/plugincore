package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.util.JsonUtil.JSON_EXTENSION
import com.vexillum.plugincore.util.JsonUtil.JSON_GLOB_MATCHER
import java.nio.file.Path
import java.util.EnumMap
import kotlin.io.path.copyTo
import kotlin.io.path.inputStream
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.toPath
import kotlin.reflect.KClass

class LanguageManager<T : Any> internal constructor(
    private val pluginCore: PluginCore,
    private val languageClass: KClass<T>,
    private val originFolderPath: String,
    private val destinationFolderPath: String
) {

    private val languages = EnumMap<LocalLanguage, Language<T>>(LocalLanguage::class.java)

    fun language(localLanguage: LocalLanguage): Language<T> =
        // Get the exact language requested
        languages[localLanguage]
            ?: with(localLanguage) {
                parent?.let { parentLocaleLanguage ->
                    // Get parent language if exact not found: AUSTRALIAN_ENGLISH -> ENGLISH
                    languages[parentLocaleLanguage]
                        // Get one of the sibling languages if the parent is not loaded: AUSTRALIAN_ENGLISH -> CANADIAN_ENGLISH
                        ?: parentLocaleLanguage.children.firstNotNullOf { siblingLocaleLanguage ->
                            languages[siblingLocaleLanguage]
                        }
                }
                    // If a language is requested and not found look for some children SPANISH -> ARGENTINIAN_SPANISH
                    ?: children.firstNotNullOfOrNull { languages[it] }
                    // Get default language if exact not found and no parents/siblings found: PORTUGUESE -> ENGLISH
                    ?: languages[DEFAULT_LANGUAGE]
                    // Get fallback/fist loaded language for unrelated language: PORTUGUESE -> SPANISH
                    ?: languages.values.firstOrNull()
                    // There are no languages loaded in the languages map
                    ?: error("Can't find any language loaded")
            }

    fun reload() {
        val originLanguages = languagePaths(originFolderPath)

        val destinationLanguages = languagePaths(destinationFolderPath)

        // Load existent language files
        destinationLanguages.forEach(this::loadLanguage)

        val loadedLocaleLanguages = languages.keys

        val destinationPath = pluginCore.dataFolder.toPath().resolve(destinationFolderPath)

        // Complement if needed or copying files from origin to destination
        originLanguages
            .filterKeys { it !in loadedLocaleLanguages }
            .forEach { (localLanguage, originLanguage) ->
                originLanguage.copyTo(destinationPath)
                loadLanguage(localLanguage, originLanguage)
            }
    }

    private fun languagePaths(relativeToDataFolder: String) =
        pluginCore.dataFolder
            .toPath()
            .resolve(relativeToDataFolder)
            .listDirectoryEntries(JSON_GLOB_MATCHER)
            .mapNotNull { destinationLanguage ->
                val fileName = destinationLanguage.fileName.toString()
                val withOutExtension = fileName.removeSuffix(JSON_EXTENSION)
                val localLanguage = LocalLanguage.ofCode(withOutExtension)
                if (localLanguage == null) {
                    pluginCore.logManager.warning(
                        "The file '$fileName' defined in $relativeToDataFolder folder doesn't match any known language format"
                    )
                }
                localLanguage?.let { it to destinationLanguage }
            }
            .toMap()

    private fun loadLanguage(localLanguage: LocalLanguage, path: Path) =
        path
            .inputStream()
            .use { inputStream ->
                try {
                    val language = Language.create(localLanguage, inputStream, languageClass)
                    languages[localLanguage] = language
                    pluginCore.logManager.info("Language ${localLanguage.languageName} successfully loaded")
                } catch (e: IllegalStateException) {
                    pluginCore.logManager.error("Failed to load language ${path.name}:\n$e")
                }
            }

    companion object {
        private val DEFAULT_LANGUAGE = ENGLISH
    }
}
