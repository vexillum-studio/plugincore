package com.vexillum.plugincore.language.message

fun Message.prepend(value: Any): Message {
    message(value) + this
    return this
}

fun Message.append(value: Any): Message =
    this + message(value)

fun Message.appendLine(value: Any? = null): Message =
    buildMessage {
        append(this)
        appendLine(value)
    }

fun Message.prependLine(): Message =
    buildMessage {
        appendLine()
        append(this)
    }

fun Message.empty(): Message =
    if (this is MessageWithReplacements) {
        MessageWithReplacements(
            message = EmptyBlock,
            messageReplacements = messageReplacements
        )
    } else {
        EmptyBlock
    }
