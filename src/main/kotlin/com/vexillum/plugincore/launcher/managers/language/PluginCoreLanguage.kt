package com.vexillum.plugincore.launcher.managers.language

import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.message.Message

data class CommandParsing(
    val boolean: Message,
    val integer: Message,
    val double: Message,
    val enum: Message,
    val player: Message,
    val world: Message,
    val vector: Message,
    val location: Message
)

data class CommandValidation(
    val numberRange: Message
)

data class CommandDescriptor(
    val color: Message,
    val accent: Message,
    val prefix: Message,
    val postfix: Message,
    val marker: Message,
    val world: Message,
    val x: Message,
    val y: Message,
    val z: Message
)

data class CommandLanguage(
    val helpMessage: Message,
    val unknownUsage: Message,
    val incorrectUsage: Message,
    val unknownError: Message,
    val permissionMessage: Message,
    val transformMessage: Message,
    val parsing: CommandParsing,
    val validation: CommandValidation,
    val descriptor: CommandDescriptor
)

data class PluginCoreLanguage(
    val prefix: Message,
    val color: Message,
    val errorColor: Message,
    val errorAccent: Message,
    val command: CommandLanguage
) : Language
