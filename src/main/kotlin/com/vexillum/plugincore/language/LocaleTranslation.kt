package com.vexillum.plugincore.language

import com.vexillum.plugincore.language.deserializer.MessageDeserializer.deserialize
import com.vexillum.plugincore.language.message.MessageReplacements
import java.io.InputStream
import kotlin.reflect.KClass

class LocaleTranslation<T : Language> private constructor(
    val localeLanguage: LocalLanguage,
    override val value: T,
    override val replacements: MessageReplacements
) : LocaleResolver<T> {

    override fun toString() =
        "${value::class.java.simpleName} LocaleTranslation($localeLanguage)"

    companion object {

        fun <T : Language> create(
            localeLanguage: LocalLanguage,
            languageStream: InputStream,
            languageClass: KClass<T>
        ): LocaleTranslation<T> {
            val result = deserialize(languageStream, languageClass)
            return LocaleTranslation(localeLanguage, result.language, result.replacements)
        }
    }
}
