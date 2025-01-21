package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.argument.Argument
import com.vexillum.plugincore.command.extractor.ArgumentExtractor
import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.managers.language.LanguageAgent
import java.util.LinkedList

class ExecutionContext<Sender : LanguageAgent> internal constructor(
    session: CommandSession<Sender>
) : CommandSession<Sender> by session {

    private val arguments = LinkedList<Argument<Sender, *>>()
    private val extractors = LinkedList<ArgumentExtractor<Sender, *>>()
    private var currentIndex: Int = -1
    private var lastExceptionIndex: Int? = null
    private var lastException: Exception? = null
    private var currentMatchingScore: Double = 0.0

    val validLastExecution: Boolean get() =
        lastExceptionIndex?.let { it > args.lastIndex } ?: true

    val executedSuccessfully: Boolean get() =
        currentIndex >= args.lastIndex

    val lastExtractor: ArgumentExtractor<Sender, *>? get() =
        extractors.getOrNull(args.lastIndex) ?: extractors.lastOrNull()

    val completed: Boolean get() =
        currentIndex == args.lastIndex && lastException == null

    val exception: Exception? get() =
        lastException

    val matchingScore: Double get() =
        currentMatchingScore

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
        argument: Argument<Sender, Type>
    ): Type =
        arguments.add(argument).let {
            argument.get(this).let { value ->
                argument.processor?.process(this, value) ?: value
            }
        }

    fun <Type : Any> getLast(
        argument: Argument<Sender, Type>
    ): Type =
        get(argument)
            .also {
                if (hasNextArgument()) {
                    throw ArgumentsNotDepletedException()
                }
            }

    fun <Type : Any> extract(
        extractor: ArgumentExtractor<Sender, Type>
    ): Type =
        extractors.add(extractor).let {
            val nextArg = next()
            currentMatchingScore += extractor.matchingScore(agent, nextArg)
            extractor.extract(this, nextArg)
        }

    private fun next(): String =
        try {
            currentIndex++
            nextArgument()
        } catch (e: NoSuchElementException) {
            throw NoNextArgumentException()
        }
}
