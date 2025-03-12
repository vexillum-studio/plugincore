package com.vexillum.plugincore.command.session

import com.vexillum.plugincore.command.Command
import com.vexillum.plugincore.command.ExecutionContext
import com.vexillum.plugincore.language.LanguageAgent

interface CommandSession<Sender : LanguageAgent> : CommandUser<Sender> {
    val command: Command<Sender>?
    val capturedInput: String
    val args: Array<String>
    val currentArg: String?
    fun hasNextArgument(): Boolean
    fun nextArgument(): String
    fun moveToNextArg(): CommandSession<Sender>
    fun resetSession(): CommandSession<Sender>
    fun executionContext(): ExecutionContext<Sender>
}
