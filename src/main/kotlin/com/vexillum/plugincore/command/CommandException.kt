package com.vexillum.plugincore.command

open class CommandException(message: String? = null) : Exception(message)

open class LanguageException(message: String) : CommandException(message)

internal class ArgumentMapException(override val cause: Exception) : CommandException()

internal class NoNextArgumentException : CommandException()

internal class ArgumentsNotDepletedException : CommandException()
