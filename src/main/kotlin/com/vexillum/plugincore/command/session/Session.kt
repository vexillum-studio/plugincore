package com.vexillum.plugincore.command.session

import java.util.regex.Pattern

internal data class Session(
    override val capturedInput: String = "",
    override val args: Array<String> = emptyArray()
) : CommandSession {

    override var original: CommandSession = this
        private set

    private var argsIterator = args.iterator()

    override val currentArg get() = args.lastOrNull()

    override fun hasNextArgument(): Boolean =
        argsIterator.hasNext()

    override fun nextArgument(): String =
        argsIterator.next()

    override fun moveToNextArg(): CommandSession {
        val newCapturedInput = buildString {
            val matcher = argumentsPattern.matcher(capturedInput)
            // Move to the next argument
            if (matcher.find()) {
                matcher.group()
            }
            // Append the rest
            while (matcher.find()) {
                append(matcher.group())
            }
        }
        val newArgs = args.copyOfRange(1, args.size)
        return copy(
            capturedInput = newCapturedInput,
            args = newArgs
        ).also {
            it.original = this.original
        }
    }

    override fun resetSession(): CommandSession {
        argsIterator = args.iterator()
        return this
    }

    override fun equals(other: Any?) =
        other is CommandSession && other === this

    override fun hashCode(): Int =
        super.hashCode()

    private companion object {
        private val argumentsPattern = Pattern.compile("(\\s*\\S\\s*)")
    }
}
