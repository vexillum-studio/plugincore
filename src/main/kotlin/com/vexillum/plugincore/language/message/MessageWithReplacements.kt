package com.vexillum.plugincore.language.message

internal class MessageWithReplacements(
    val message: Message,
    val messageReplacements: MessageReplacements
) : Message {

    override val raw = message.raw

    override val strippedLength
        get() =
            stripped().length

    override fun resolveString(replacements: Map<String, Any>): String =
        message.resolveString(replacements)

    override fun resolved(): String =
        resolveString(messageReplacements.replacements)

    override fun repeat(times: Int): MessageWithReplacements =
        MessageWithReplacements(
            message = message.repeat(times),
            messageReplacements = messageReplacements
        )

    operator fun plus(other: MessageWithReplacements): MessageWithReplacements =
        MessageWithReplacements(
            message = message + other.message,
            messageReplacements = messageReplacements + other.messageReplacements
        )

    override operator fun plus(message: Message?): MessageWithReplacements =
        if (message is MessageWithReplacements) {
            plus(message)
        } else {
            MessageWithReplacements(
                message = this.message + message,
                messageReplacements = messageReplacements
            )
        }

    override fun replace(key: String, value: Any): Message {
        messageReplacements.replace(key, value)
        return this
    }

    override fun replacing(vararg replacements: Pair<String, Any>): Message {
        messageReplacements.replace(*replacements)
        return this
    }

    override fun replacingWith(replacements: Map<String, Any>): Message {
        messageReplacements.replaceAll(replacements)
        return this
    }

    override fun toString(): String = resolved()

    override fun equals(other: Any?): Boolean =
        if (other is String) resolved() == other
        else (other as? MessageWithReplacements)?.resolved() == resolved()

    override fun hashCode(): Int =
        resolved().hashCode()
}
