package com.vexillum.plugincore.language.message

fun message(
    message: Any,
    replacements: MessageReplacements? = null
): Message {
    val castedMessage = if (message is Message) {
        message
    } else {
        MessageBlock(message.toString())
    }
    return message(castedMessage, replacements)
}

fun message(
    message: Message,
    replacements: MessageReplacements? = null
): Message {
    return if (replacements == null || replacements.replacements.isEmpty()) {
        message
    } else {
        if (message is MessageWithReplacements) {
            MessageWithReplacements(
                message = message.message,
                messageReplacements = message.messageReplacements + replacements
            )
        } else {
            MessageWithReplacements(message, replacements)
        }
    }
}

fun <T> messageFactory(
    block: MessageFactory.() -> T
): T =
    object : MessageFactory {}.run(block)

interface MessageFactory {

    fun param(key: String): ParameterBlock =
        ParameterBlock("{$key}")

    fun msg(value: Any): MessageBlock =
        MessageBlock(value.toString())

    fun repl(key: String, replaced: Any): ReplacedBlock =
        ReplacedBlock(key, replaced.toString())

    fun compound(vararg messages: Message): CompoundMessage =
        CompoundMessage(arrayOf(*messages))

    fun messageOf(vararg parts: Any): Message =
        parts.fold(EmptyBlock as Message) { acc, part ->
            when (part) {
                is Message -> acc + part
                else -> acc + message(part)
            }
        }

    fun messageOf(parts: Collection<Any>): Message =
        parts.fold(EmptyBlock as Message) { acc, part ->
            when (part) {
                is Message -> acc + part
                else -> acc + message(part)
            }
        }
}
