package com.vexillum.plugincore.language

import org.bukkit.ChatColor

fun message(
    message: Any,
): LanguageMessage =
    SimpleLanguageMessage(message.toString())

fun message(
    message: Message,
    replacements: Map<String, Any> = emptyMap()
): LanguageMessage =
    SimpleLanguageMessage(
        message,
        replacements.toMutableMap()
    )

fun buildMessage(
    block: LanguageMessageBuilder.() -> Unit
): LanguageMessage =
    LanguageMessageBuilder().run {
        block()
        build()
    }

sealed interface LanguageMessage {
    val strippedLength: Int
    fun replace(replacements: Map<String, Any>): LanguageMessage
    fun replaceKey(key: String, value: String): LanguageMessage
    fun resolved(): String
    fun stripped(): String
    fun repeat(times: Int): LanguageMessage
    fun mutate(block: (Message, Map<String, Any>) -> LanguageMessage): LanguageMessage
    operator fun plus(other: LanguageMessage): LanguageMessage
    operator fun plus(message: Message): LanguageMessage
    operator fun invoke(): String
}

private class SimpleLanguageMessage(
    val message: Message,
    val replacements: MutableMap<String, Any>
) : LanguageMessage {

    override val strippedLength get() =
        stripped().length

    constructor(
        value: String,
        replacements: Map<String, Any> = emptyMap()
    ) : this(
        message = MessageBlock(value),
        replacements = replacements.toMutableMap()
    )

    override fun replace(replacements: Map<String, Any>): LanguageMessage {
        this.replacements.putAll(replacements)
        return this
    }

    override fun replaceKey(key: String, value: String): LanguageMessage {
        replacements[key] = value
        return this
    }

    override fun resolved(): String =
        message.resolveString(replacements)

    override fun stripped(): String =
        ChatColor.stripColor(resolved()) ?: resolved()

    override fun repeat(times: Int): LanguageMessage =
        SimpleLanguageMessage(
            message = message.repeat(times),
            replacements = replacements
        )

    override fun mutate(block: (Message, Map<String, Any>) -> LanguageMessage): LanguageMessage =
        block(message, replacements)

    override operator fun plus(other: LanguageMessage): LanguageMessage =
        if (other is SimpleLanguageMessage)
            SimpleLanguageMessage(
                message = message + other.message,
                replacements = (replacements + other.replacements).toMutableMap()
            )
        else this

    override fun plus(message: Message): LanguageMessage =
        SimpleLanguageMessage(
            message = this.message + message,
            replacements = replacements
        )

    override fun invoke() = resolved()

    override fun toString(): String = resolved()

    override fun equals(other: Any?): Boolean =
        if (other is String) resolved() == other
        else (other as? LanguageMessage)?.resolved() == resolved()

    override fun hashCode(): Int =
        resolved().hashCode()
}

class LanguageMessageBuilder(
    initialMessage: Any? = null
) {

    private var mark: Int = 0

    private var message: LanguageMessage =
        if (initialMessage != null) {
            if (initialMessage is LanguageMessage) {
                initialMessage
            } else {
                SimpleLanguageMessage(initialMessage.toString())
            }
        } else {
            SimpleLanguageMessage(EMPTY)
        }

    fun mark() {
        this.mark = message.stripped().length
    }

    fun measure(): Int =
        message.stripped().lastIndex - mark

    fun append(message: LanguageMessage) {
        this.message += message
    }

    fun append(value: Any) {
        if (value is LanguageMessage) {
            append(value)
        } else {
            message += SimpleLanguageMessage(value.toString())
        }
    }

    fun appendSpace(times: Int = 1) {
        append(SPACE.repeat(times))
    }

    fun appendLine(message: Any? = null) {
        append(LINE_BREAK)
        if (message != null) {
            append(message)
        }
    }

    fun appendParam(key: String) {
        message += ParameterBlock("{$key}")
    }

    fun appendReplacement(key: String, initialValue: String) {
        message += ReplacedBlock(key, initialValue)
    }

    fun <T : Any> Collection<T>.joinMessage(
        separator: Any = COMMA,
        prefix: Any? = null,
        postfix: Any? = null,
        block: (T.() -> Any)? = null
    ) {
        val lastIndex = size - 1
        if (prefix != null) {
            append(prefix)
        }
        forEachIndexed { index, item ->
            append(block?.let { it(item) } ?: item)
            if (index < lastIndex) {
                append(separator)
            }
        }
        if (postfix != null) {
            append(postfix)
        }
    }

    fun build(): LanguageMessage =
        message

    companion object {
        private const val SPACE = " "
        private const val EMPTY = ""
        private const val COMMA = " ,"
        private const val LINE_BREAK = "\n"
    }
}
