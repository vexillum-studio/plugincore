package com.vexillum.plugincore.command.extractor

import com.vexillum.plugincore.command.ArgumentsNotDepletedException
import com.vexillum.plugincore.command.NoNextArgumentException
import com.vexillum.plugincore.command.argument.Argument
import com.vexillum.plugincore.managers.language.LanguageAgent

class ExecutionContext(
    args: Array<String>
) {

    private val argsIterator = args.iterator()

    lateinit var currentArgument: String

    fun <Sender : LanguageAgent, Type : Any> get(
        sender: Sender,
        argument: Argument<Sender, Type>
    ): Type =
        argument.get(sender, this).let { value ->
            argument.processor?.process(sender, value) ?: value
        }

    fun <Sender : LanguageAgent, Type : Any> getLast(
        sender: Sender,
        argument: Argument<Sender, Type>
    ): Type =
        get(sender, argument)
            .also {
                if (argsIterator.hasNext()) {
                    throw ArgumentsNotDepletedException()
                }
            }

    fun hasNextArgument() =
        argsIterator.hasNext()

    fun nextArgument() =
        try {
            argsIterator.next().also { currentArgument = it }
        } catch (e: NoSuchElementException) {
            throw NoNextArgumentException()
        }
}
