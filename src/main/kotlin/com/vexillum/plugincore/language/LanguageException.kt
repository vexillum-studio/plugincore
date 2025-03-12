package com.vexillum.plugincore.language

import com.vexillum.plugincore.language.message.Message

open class LanguageException(
    val languageMessage: Message
) : Exception(languageMessage.resolved()) {
    override val message = languageMessage.resolved()
}
