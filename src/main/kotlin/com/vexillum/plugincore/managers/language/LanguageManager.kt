package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.extensions.copyResourceTo
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LocalLanguage
import com.vexillum.plugincore.language.LocaleTranslation
import com.vexillum.plugincore.language.MissingLanguageKeyException
import com.vexillum.plugincore.language.context.LanguageContext
import com.vexillum.plugincore.util.JsonUtil.JSON_EXTENSION
import com.vexillum.plugincore.util.JsonUtil.JSON_GLOB_MATCHER
import java.io.File
import java.nio.file.Path
import java.util.EnumMap
import kotlin.io.path.absolutePathString
import kotlin.io.path.copyTo
import kotlin.io.path.exists
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.reflect.KClass

class LanguageManager<T : Language> internal constructor(
    override val pluginCore: PluginCore,
    private val languageClass: KClass<T>,
    private val originFolderPath: String,
    private val destinationFolderPath: String
) : LanguageContext<T> {

    private val languages = EnumMap<LocalLanguage, LocaleTranslation<T>>(LocalLanguage::class.java)

    val loadedLanguages get() = languages.toMap()

    override fun translation(localLanguage: LocalLanguage): LocaleTranslation<T> =
        // Get the exact language requested
        languages[localLanguage]
            ?: with(localLanguage) {
                parent?.let { parentLocaleLanguage ->
                    // Get parent language if exact not found: AUSTRALIAN_ENGLISH -> ENGLISH
                    languages[parentLocaleLanguage]
                        // Get one of the sibling languages if the parent is not loaded: AUSTRALIAN_ENGLISH -> CANADIAN_ENGLISH
                        ?: parentLocaleLanguage.children.firstNotNullOfOrNull { siblingLocaleLanguage ->
                            languages[siblingLocaleLanguage]
                        }
                }
                    // If a language is requested and not found look for some children SPANISH -> ARGENTINIAN_SPANISH
                    ?: children.firstNotNullOfOrNull { languages[it] }
                    // Get default language if exact not found and no parents/siblings found: PORTUGUESE -> ENGLISH
                    ?: languages[LocalLanguage.DEFAULT]
                    // Get fallback/fist loaded language for unrelated language: PORTUGUESE -> SPANISH
                    ?: languages.values.firstOrNull()
                    // There are no languages loaded in the languages map
                    ?: error("Can't find any language loaded")
            }

    fun reload() {
        createDestinationFolder()
        useOriginPath { originPath ->

            val destinationPath = pluginCore.plugin.dataFolder.toPath().resolve(destinationFolderPath)
                .also { require(it.isDirectory()) { "Destination folder must be a directory" } }

            val originLanguages = languagePaths(originPath)
            val destinationLanguages = languagePaths(destinationPath)

            // Load existent language files
            destinationLanguages.forEach(this::loadLanguage)

            val loadedLocaleLanguages = languages.keys
            // Complement if needed or copying files from origin to destination
            originLanguages
                .filterKeys { it !in loadedLocaleLanguages }
                .forEach { (localLanguage, originLanguage) ->
                    val fileName = originLanguage.fileName
                    val destinationFilePath = destinationPath.resolve(fileName)
                    if (!destinationFilePath.exists()) {
                        pluginCore.logManager.info("Trying to import origin language file: $fileName")
                        loadLanguage(localLanguage, originLanguage)
                        originLanguage.copyTo(destinationFilePath, true)
                        pluginCore.logManager.info("Imported missing origin language file: $fileName")
                    } else {
                        pluginCore.logManager.warning("Loading origin language file: $fileName as fallback")
                        loadLanguage(localLanguage, originLanguage)
                    }
                }
        }
    }

    private fun useOriginPath(block: (Path) -> Unit) {
        val tempFile = File(pluginCore.plugin.dataFolder, "$destinationFolderPath${File.separator}$TEMP_PATH")
        try {
            // Delete temp folder if exists and then create a fresh one
            tempFile.deleteRecursively()
            tempFile.mkdirs()
            val tempPath = tempFile.toPath().also {
                require(it.isDirectory()) { "Origin folder must be a directory" }
            }
            pluginCore::class.copyResourceTo(originFolderPath, tempPath)
            block(tempPath)
        } finally {
            tempFile.deleteRecursively()
        }
    }

    private fun createDestinationFolder() {
        val folderName = "${pluginCore.plugin.name}${File.separator}$destinationFolderPath"
        try {
            if (File(pluginCore.plugin.dataFolder, destinationFolderPath).mkdirs()) {
                pluginCore.logManager.info("Created language folder in: $folderName")
            }
        } catch (e: Exception) {
            pluginCore.logManager.error("Error creating language folder $folderName: $e")
        }
    }

    private fun languagePaths(path: Path) =
        path
            .listDirectoryEntries(JSON_GLOB_MATCHER)
            .mapNotNull { destinationLanguage ->
                val fileName = destinationLanguage.fileName.toString()
                val withOutExtension = fileName.removeSuffix(JSON_EXTENSION)
                val localLanguage = LocalLanguage.ofCode(withOutExtension)
                if (localLanguage == null) {
                    pluginCore.logManager.warning(
                        "The file '$fileName' defined in $path folder doesn't match any known language format"
                    )
                }
                localLanguage?.let { it to destinationLanguage }
            }
            .toMap()

    private fun loadLanguage(localLanguage: LocalLanguage, path: Path) =
        path.inputStream().let { inputStream ->
            try {
                try {
                    val localeTranslation = LocaleTranslation.create(localLanguage, inputStream, languageClass)
                    languages[localLanguage] = localeTranslation
                    pluginCore.logManager.info("${localLanguage.languageName} language successfully loaded")
                } catch (e: MissingLanguageKeyException) {
                    e.throwWithFileName(path.absolutePathString())
                }
            } catch (e: Exception) {
                pluginCore.logManager.error("Failed to load language ${path.name}:")
                throw e
            }
        }

    companion object {
        private const val TEMP_PATH = "origin"
    }
}
