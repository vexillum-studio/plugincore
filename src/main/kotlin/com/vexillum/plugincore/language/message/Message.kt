package com.vexillum.plugincore.language.message

import com.vexillum.plugincore.extensions.combineEdges
import com.vexillum.plugincore.extensions.plus
import com.vexillum.plugincore.extensions.repeat
import com.vexillum.plugincore.extensions.replaceFirst
import com.vexillum.plugincore.extensions.replaceLast
import com.vexillum.plugincore.extensions.trimEdges
import org.bukkit.ChatColor

interface Message : MessageFactory {

    val raw: String
    val size: Int get() = 1
    val strippedLength get() = stripped().length

    fun resolved(): String = raw.postProcess()

    fun stripped(): String =
        ChatColor.stripColor(resolved()) ?: resolved()

    fun resolveString(replacements: Map<String, Any> = emptyMap()): String

    fun repeat(times: Int): Message =
        if (times > 0) {
            messageOf(Array(times) { this }.toList())
        } else {
            this
        }

    fun mutate(block: MutateMessageBlock.() -> Unit): Message =
        MutateMessageBlock().run {
            block()
            mutate(this@Message)
        }

    operator fun plus(message: Message?): Message =
        if (message == null || message.raw.isEmpty()) {
            this
        } else if (message is MessageWithReplacements) {
            message.plus(this)
        } else if (message is CompoundMessage) {
            val merged = this + message.messages.first()
            if (merged is CompoundMessage) {
                CompoundMessage(this.plus(message.messages))
            } else {
                CompoundMessage(message.messages.replaceFirst(merged))
            }
        } else {
            CompoundMessage(arrayOf(this, message))
        }

    operator fun invoke(): String =
        resolved()

    fun replace(key: String, value: Any): Message {
        val messageReplacements = messageReplacements()
        messageReplacements.replace(key, value)
        return message(this, messageReplacements)
    }

    fun replacing(vararg replacements: Pair<String, Any>): Message {
        val messageReplacements = messageReplacements()
        messageReplacements.replace(*replacements)
        return message(this, messageReplacements)
    }

    fun replacingWith(replacements: Map<String, Any>): Message {
        val messageReplacements = messageReplacements(replacements)
        return message(this, messageReplacements)
    }

    fun String.postProcess() =
        ChatColor.translateAlternateColorCodes(COLOR_CHAR, this)

    companion object {
        private const val COLOR_CHAR = '&'
    }
}

internal object EmptyBlock : Message {
    override val raw = ""
    override val size = 0

    override fun resolveString(replacements: Map<String, Any>) =
        raw

    override operator fun plus(message: Message?): Message =
        when {
            (message == null || message is EmptyBlock) -> this
            else -> message
        }
}

open class CompoundMessage internal constructor(
    internal val messages: Array<Message>
) : Message {

    override val raw by lazy {
        resolveString()
    }

    override val size = messages.sumOf { it.size }

    override fun resolveString(replacements: Map<String, Any>): String =
        buildString {
            for (message in messages) {
                append(message.resolveString(replacements))
            }
        }

    override fun repeat(times: Int): Message =
        if (times > 0) {
            CompoundMessage(messages.repeat(times))
        } else {
            this
        }

    override operator fun plus(message: Message?): Message =
        when (message) {
            null -> this
            is EmptyBlock -> this
            is CompoundMessage -> {
                val merged = messages.last() + message.messages.first()
                if (merged is CompoundMessage) {
                    CompoundMessage(messages + message.messages)
                } else {
                    CompoundMessage(messages.combineEdges(message.messages, merged))
                }
            }

            else ->
                if (message.raw.isEmpty()) {
                    this
                } else {
                    val merged = messages.last() + message
                    if (merged is CompoundMessage) {
                        CompoundMessage(messages + message)
                    } else {
                        CompoundMessage(messages.replaceLast(merged))
                    }
                }
        }

    override fun toString(): String = raw

    override fun equals(other: Any?): Boolean =
        if (other is String) resolved() == other
        else (other as? CompoundMessage)?.resolved() == resolved()

    override fun hashCode(): Int =
        messages.hashCode()
}

class MessageList internal constructor(
    private val internalMessages: List<Message>
) : CompoundMessage(internalMessages.toTypedArray()), List<Message> by internalMessages {

    override val raw = messages.joinToString(separator = "\n") { it.raw }

    override fun resolveString(replacements: Map<String, Any>) =
        messages.joinToString(separator = "\n") {
            it.resolveString(replacements)
        }

    override operator fun plus(message: Message?): Message =
        when {
            message is MessageList -> MessageList(internalMessages + message.internalMessages)
            else -> super.plus(message)
        }

    override fun toString() = raw

    override fun equals(other: Any?): Boolean =
        if (other is String) resolved() == other
        else (other as? MessageList)?.resolved() == resolved()

    override fun hashCode(): Int =
        internalMessages.hashCode()

    override val size: Int
        get() = internalMessages.size
}

data class MessageBlock internal constructor(val block: String) : Message {

    override val raw = block

    override fun resolveString(replacements: Map<String, Any>) = block.postProcess()

    override fun repeat(times: Int): MessageBlock =
        if (times > 0) MessageBlock(block.repeat(times)) else this

    override operator fun plus(message: Message?): Message =
        if (message != null && block.isEmpty())
            message
        else if (message is MessageBlock)
            if (message.block.isEmpty()) this
            else MessageBlock(block + message.block)
        else super.plus(message)

    override fun toString() = block

    override fun equals(other: Any?): Boolean =
        if (other is String) resolved() == other
        else (other as? MessageBlock)?.block == block

    override fun hashCode(): Int =
        block.hashCode()
}

data class ParameterBlock internal constructor(private val enclosed: String) : Message {

    override val raw = enclosed

    val key: String = enclosed.trimEdges()

    override fun resolveString(replacements: Map<String, Any>): String =
        (replacements[key]?.toString() ?: enclosed).postProcess()

    override fun toString(): String = enclosed

    override fun equals(other: Any?): Boolean =
        if (other is String) resolved() == other
        else (other as? ParameterBlock)?.enclosed == enclosed

    override fun hashCode(): Int =
        enclosed.hashCode()
}

data class ReplacedBlock internal constructor(
    val key: String,
    val replaced: String
) : Message {

    override val raw = replaced

    override fun resolveString(replacements: Map<String, Any>): String =
        (replacements[key]?.toString() ?: replaced).postProcess()

    override fun toString(): String = raw

    override fun equals(other: Any?): Boolean =
        if (other is String) resolved() == other
        else (other as? ReplacedBlock)?.let {
            it.replaced == replaced && it.key == key
        } ?: false

    override fun hashCode(): Int =
        key.hashCode()
}
