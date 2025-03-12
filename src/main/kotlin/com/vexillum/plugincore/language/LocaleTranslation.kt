package com.vexillum.plugincore.language

import java.io.InputStream
import kotlin.reflect.KClass

class LocaleTranslation<T : Language> private constructor(
    val localeLanguage: LocalLanguage,
    override val value: T
) : LocaleResolver<T> {

    override fun toString() =
        "LocaleTranslation($localeLanguage)"

    companion object {

        fun <T : Language> create(
            localeLanguage: LocalLanguage,
            languageStream: InputStream,
            languageClass: KClass<T>
        ): LocaleTranslation<T> {
            val deserializer = LanguageDeserializer(languageClass)
            val languageValue = deserializer.parseLanguage(languageStream)
            return LocaleTranslation(localeLanguage, languageValue)
        }
    }
}
