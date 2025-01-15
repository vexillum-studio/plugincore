package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.argument.Argument
import com.vexillum.plugincore.command.extractor.ArgumentExtractor
import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.managers.language.LanguageAgent
import java.util.LinkedList
import java.util.regex.Pattern

class ExecutionContext<Sender : LanguageAgent>(
    private val session: CommandSession
) {

    private val argsIterator = session.currentArgs.copyOf().iterator()

    private val arguments = LinkedList<Argument<Sender, *>>()
    private val extractors = LinkedList<ArgumentExtractor<Sender, *>>()
    private var currentIndex: Int = -1
    private var lastExceptionIndex: Int? = null
    private var lastException: Exception? = null

    val currentArg
        get() =
            session.currentArgs.lastOrNull()

    val validLastExecution: Boolean
        get() =
            lastExceptionIndex?.let { it > session.currentArgs.lastIndex } ?: true

    val executedSuccessfully: Boolean get() =
        currentIndex >= session.currentArgs.lastIndex

    val lastExtractor: ArgumentExtractor<Sender, *>?
        get() = extractors.getOrNull(session.currentArgs.lastIndex) ?: extractors.lastOrNull()

    fun readToEnd() =
        buildString {
            val matcher = argumentsPattern.matcher(session.capturedInput)
            var index = 0
            while (matcher.find()) {
                if (index > currentIndex) {
                    append(matcher.group())
                    nextArgument()
                }
                index++
            }
        }

    fun safeApply(
        block: ExecutionContext<Sender>.() -> Any
    ): ExecutionContext<Sender> =
        apply {
            try {
                block()
            } catch (e: Exception) {
                lastException = e
                lastExceptionIndex = currentIndex
            }
        }

    fun <Type : Any> get(
        sender: Sender,
        argument: Argument<Sender, Type>
    ): Type =
        arguments.add(argument).let {
            argument.get(sender, this).let { value ->
                argument.processor?.process(sender, value) ?: value
            }
        }

    fun <Type : Any> getLast(
        sender: Sender,
        argument: Argument<Sender, Type>
    ): Type =
        get(sender, argument)
            .also {
                if (hasNextArgument()) {
                    throw ArgumentsNotDepletedException()
                }
            }

    fun <Type : Any> extract(
        sender: Sender,
        extractor: ArgumentExtractor<Sender, Type>
    ): Type =
        extractors.add(extractor).let {
            extractor.extract(sender, nextArgument())
        }

    fun hasNextArgument(): Boolean =
        argsIterator.hasNext()

    private fun nextArgument(): String =
        try {
            currentIndex++
            argsIterator.next()
        } catch (e: NoSuchElementException) {
            throw NoNextArgumentException()
        }

    private companion object {
        private val argumentsPattern = Pattern.compile("(\\s*\\S\\s*)")
    }
}
