package com.vexillum.plugincore.launcher.managers.language

import com.vexillum.plugincore.managers.language.Message

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

data class CommandDescriptor(
    val color: Message,
    val accent: Message,
    val prefix: Message,
    val postfix: Message,
    val world: Message,
    val x: Message,
    val y: Message,
    val z: Message
)

data class CommandLanguage(
    val prefix: Message,
    val errorColor: Message,
    val errorAccent: Message,
    val permissionMessage: Message,
    val transformMessage: Message,
    val parsing: CommandParsing,
    val descriptor: CommandDescriptor
)

data class PluginCoreLanguage(
    val command: CommandLanguage
)
