package com.vexillum.plugincore.command.session

import com.vexillum.plugincore.command.Command
import com.vexillum.plugincore.command.ExecutionContext
import com.vexillum.plugincore.language.LanguageAgent
import java.util.regex.Pattern

internal class Session<Sender : LanguageAgent>(
    override val command: Command<Sender>?,
    private val user: CommandUser<Sender>,
    override val capturedInput: String,
    override val args: Array<String>
) : CommandSession<Sender>, CommandUser<Sender> by user {

    constructor(
        sender: Sender,
        capturedInput: String = "",
        args: Array<String> = emptyArray()
    ) : this(
        command = null,
        user = User(sender),
        capturedInput = capturedInput,
        args = args
    )

    private var argsIterator = args.iterator()

    override val currentArg get() = args.lastOrNull()

    override fun hasNextArgument(): Boolean =
        argsIterator.hasNext()

    override fun nextArgument(): String =
        argsIterator.next()

    override fun moveToNextArg(): CommandSession<Sender> {
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
        )
    }

    override fun resetSession(): CommandSession<Sender> {
        argsIterator = args.iterator()
        return this
    }

    override fun executionContext(): ExecutionContext<Sender> =
        ExecutionContext(this)

    fun copy(
        command: Command<Sender>? = this.command,
        user: CommandUser<Sender> = this.user,
        capturedInput: String = this.capturedInput,
        args: Array<String> = this.args
    ): Session<Sender> =
        Session(
            command = command,
            user = user,
            capturedInput = capturedInput,
            args = args
        )

    override fun equals(other: Any?) =
        other is CommandSession<*> && other === this

    override fun hashCode(): Int =
        super.hashCode()

    private companion object {
        private val argumentsPattern = Pattern.compile("(\\s*\\S\\s*)")
    }
}
