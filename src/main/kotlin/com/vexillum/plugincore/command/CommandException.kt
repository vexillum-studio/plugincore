package com.vexillum.plugincore.command

import com.vexillum.plugincore.language.LanguageException
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.message

open class CommandException(message: Message) : LanguageException(message) {
    constructor(message: String) : this(message(message))
}

internal class ArgumentExtractException(
    message: Message,
    val descriptor: Message
) : CommandException(message)

internal class ArgumentMapException(
    override val cause: Exception
) : CommandException("Can't map argument")

internal class NoNextArgumentException :
    CommandException("No next argument")

internal class ArgumentsNotDepletedException :
    CommandException("Arguments not depleted")
