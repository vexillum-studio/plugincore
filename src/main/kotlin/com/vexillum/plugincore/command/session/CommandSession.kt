package com.vexillum.plugincore.command.session

interface CommandSession {
    val original: CommandSession
    val capturedInput: String
    val args: Array<String>
    val currentArg: String?
    fun hasNextArgument(): Boolean
    fun nextArgument(): String
    fun moveToNextArg(): CommandSession
    fun resetSession(): CommandSession
}
