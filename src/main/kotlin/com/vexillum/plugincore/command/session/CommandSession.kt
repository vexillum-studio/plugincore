package com.vexillum.plugincore.command.session

data class CommandSession(
    var capturedInput: String = "",
    var currentArgs: Array<String> = emptyArray()
) {

    override fun equals(other: Any?) =
        other is CommandSession && other === this

    override fun hashCode(): Int =
        super.hashCode()
}
