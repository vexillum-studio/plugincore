package com.vexillum.plugincore.managers.language

import java.util.Locale

enum class LocalLanguage(
    val languageName: String,
    val language: String,
    val country: String? = null,
    val parent: LocalLanguage? = null
) {
    ENGLISH("English", "en"),
    AUSTRALIAN_ENGLISH("Australian English", "en", "ca", ENGLISH),
    CANADIAN_ENGLISH("Canadian English", "en", "ca", ENGLISH),
    UNITED_STATES_ENGLISH("English US", "en", "us", ENGLISH),

    FRENCH("Français", "fr"),
    FRENCH_FR("Français", "fr", "fr", FRENCH),
    FRENCH_CA("Français", "fr", "ca", FRENCH),

    PORTUGUESE("Português", "pt"),
    PORTUGUESE_BR("Português", "pt", "br", PORTUGUESE),
    PORTUGUESE_PT("Português", "pt", "pt", PORTUGUESE),

    SPANISH("Español", "es"),
    SPAIN_SPANISH("Español", "es", "es", SPANISH),
    ARGENTINEAN_SPANISH("Español Argentino", "es", "ar", SPANISH),
    MEXICO_SPANISH("Español México", "es", "mx", SPANISH),
    URUGUAY_SPANISH("Español Uruguay", "es", "uy", SPANISH),
    VENEZUELA_SPANISH("Español Venezuela", "es", "ve", SPANISH),
    CATALAN("Català", "ca", "es", SPANISH),

    GAEILGE("Gaeilge", "ga", "ie"),
    GALICIAN("Galego", "gl", "es"),

    GERMAN("Deutsch", "de", "de"),

    ITALIAN("Italiano", "it", "it"),

    JAPANESE("日本語", "ja", "jp"),
    KOREAN("한국어", "ko", "kr"),

    CHINESE("简体中文", "zh"),
    SIMPLIFIED_CHINESE("简体中文", "zh", "cn", CHINESE),
    TRADITIONAL_CHINESE("繁體中文", "zh", "tw", CHINESE),

    DUTCH("Nederlands", "nl", "nl"),

    RUSSIAN("Русский", "ru", "ru"),

    POLISH("Polski", "pl", "pl");

    val languageTag = language + (country?.let { "$LOCALE_SEPARATOR${country.uppercase()}" } ?: "")

    val locale = Locale.forLanguageTag(languageTag)

    /**
     * Returns the with ISO 15897 language and country code in lowercase, example: en_us
     */
    val code = language.lowercase().plus((country?.let { "$SEPARATOR$it" }) ?: "")

    val children by lazy {
        childrenLanguages[this] ?: emptyList()
    }

    companion object {

        private const val SEPARATOR = "_"
        private const val LOCALE_SEPARATOR = "-"
        val DEFAULT = ENGLISH

        private val childrenLanguages =
            values()
                .filter { it.parent != null }
                .map { it.parent!! to it }
                .groupBy { it.first }
                .mapValues { it.value.map { pair -> pair.second } }

        private val languagesByCode = values()
            .asSequence()
            .associateBy {
                it.code
            }

        fun ofCode(code: String): LocalLanguage? =
            languagesByCode[code.lowercase()]
    }
}
