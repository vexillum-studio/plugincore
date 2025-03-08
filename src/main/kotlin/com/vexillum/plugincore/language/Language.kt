package com.vexillum.plugincore.language

import java.io.InputStream
import kotlin.reflect.KClass

class Language<out T : Any> private constructor(
    val localeLanguage: LocalLanguage,
    internal val language: T
) {

    override fun toString() =
        "Language($localeLanguage)"

    companion object {

        fun <T : Any> create(
            localeLanguage: LocalLanguage,
            languageStream: InputStream,
            languageClass: KClass<T>
        ): Language<T> {
            val deserializer = LanguageDeserializer(languageClass)
            val languageValue = deserializer.parseLanguage(languageStream)
            return Language(localeLanguage, languageValue)
        }
    }
}

fun <T : Any> Language<T>.resolve(
    replacements: Map<String, Any> = emptyMap(),
    block: T.() -> Message
): LanguageMessage =
    language.run(block).resolve(replacements)
