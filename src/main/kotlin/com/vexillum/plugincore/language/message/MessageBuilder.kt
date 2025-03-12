package com.vexillum.plugincore.language.message
import com.vexillum.plugincore.language.message.message as createMessage

fun buildMessage(
    block: MessageBuilder.() -> Unit
): Message =
    MessageBuilder().run {
        block()
        build()
    }

class MessageBuilder(
    initialMessage: Any? = null
) : MessageFactory {

    private var mark: Int = 0

    private var message: Message =
        if (initialMessage != null) {
            if (initialMessage is Message) {
                initialMessage
            } else {
                createMessage(initialMessage)
            }
        } else {
            emptyMessage
        }

    fun mark() {
        this.mark = message.strippedLength
    }

    fun measure(): Int =
        message.stripped().lastIndex - mark

    fun append(message: Message): Message {
        this.message += message
        return message
    }

    fun append(value: Any): Message =
        if (value is Message) {
            append(value)
        } else {
            msg(value.toString()).also {
                message += it
            }
        }

    fun appendSpace(times: Int = 1): Message =
        append(spaceMessage.repeat(times))

    fun appendLine(message: Any? = null): Message {
        var appended = append(lineBreakMessage)
        if (message != null) {
            appended += append(message)
        }
        return appended
    }

    fun appendParam(key: String): Message =
        param(key).also {
            message += it
        }

    fun appendReplacement(key: String, initialValue: String) =
        repl(key, initialValue).also {
            message += it
        }

    fun <T : Any> Collection<T>.joinMessage(
        separator: Any = commaMessage,
        prefix: Any? = null,
        postfix: Any? = null,
        block: (T.() -> Any)? = null
    ): Message {
        var appended: Message = emptyMessage
        val lastIndex = size - 1
        if (prefix != null) {
            appended += append(prefix)
        }
        forEachIndexed { index, item ->
            appended += append(block?.let { it(item) } ?: item)
            if (index < lastIndex) {
                appended += append(separator)
            }
        }
        if (postfix != null) {
            appended += append(postfix)
        }
        return appended
    }

    fun build(): Message =
        message

    companion object {
        private val spaceMessage = MessageBlock(" ")
        private val emptyMessage = MessageBlock("")
        private val commaMessage = MessageBlock(" ,")
        private val lineBreakMessage = MessageBlock("\n")
    }
}
