package com.vexillum.plugincore.language

open class LanguageException(
    val languageMessage: LanguageMessage
) : Exception(languageMessage.toString()) {
    override val message = languageMessage.toString()
}
