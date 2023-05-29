package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.argument.Argument
import com.vexillum.plugincore.command.extractor.ExecutionContext
import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.util.Constants.SPACE

interface CommandUsage<Sender : LanguageAgent> {

    val arguments: List<Argument<Sender, *>>

    fun execute(sender: Sender, args: Array<String>)

    fun describe(agent: LanguageAgent): String =
        PluginCoreLauncher.instance.withAgent(agent) {
            val color = resolve { command.descriptor.color }
            val prefix = resolve { command.descriptor.prefix }
            val postfix = resolve { command.descriptor.postfix }
            arguments
                .asSequence()
                .flatMap { it.extractors }
                .map { color + it.descriptor(agent) }
                .joinToString(
                    separator = SPACE,
                    prefix = prefix,
                    postfix = postfix
                )
        }
}

internal class CommandUsage0<Sender : LanguageAgent>(
    val block: (Sender) -> Unit
) : CommandUsage<Sender> {

    override val arguments = emptyList<Argument<Sender, *>>()

    override fun execute(sender: Sender, args: Array<String>) {
        block(sender)
    }
}

internal class CommandUsage1<Sender : LanguageAgent, T1 : Any>(
    private val arg1: Argument<Sender, T1>,
    val block: (Sender, T1) -> Unit
) : CommandUsage<Sender> {

    override val arguments = listOf(arg1)

    override fun execute(sender: Sender, args: Array<String>) {
        with(ExecutionContext(args)) {
            block(sender, getLast(sender, arg1))
        }
    }
}

internal class CommandUsage2<Sender : LanguageAgent, T1 : Any, T2 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val block: (Sender, T1, T2) -> Unit
) : CommandUsage<Sender> {

    override val arguments = listOf(arg1, arg2)

    override fun execute(sender: Sender, args: Array<String>) {
        with(ExecutionContext(args)) {
            block(
                sender,
                get(sender, arg1),
                getLast(sender, arg2)
            )
        }
    }
}

internal class CommandUsage3<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val arg3: Argument<Sender, T3>,
    private val block: (Sender, T1, T2, T3) -> Unit
) : CommandUsage<Sender> {

    override val arguments = listOf(arg1, arg2, arg3)

    override fun execute(sender: Sender, args: Array<String>) {
        with(ExecutionContext(args)) {
            block(
                sender,
                get(sender, arg1),
                get(sender, arg2),
                getLast(sender, arg3)
            )
        }
    }
}

internal class CommandUsage4<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val arg3: Argument<Sender, T3>,
    private val arg4: Argument<Sender, T4>,
    private val block: (Sender, T1, T2, T3, T4) -> Unit
) : CommandUsage<Sender> {

    override val arguments = listOf(arg1, arg2, arg3, arg4)

    override fun execute(sender: Sender, args: Array<String>) {
        with(ExecutionContext(args)) {
            block(
                sender,
                get(sender, arg1),
                get(sender, arg2),
                get(sender, arg3),
                getLast(sender, arg4)
            )
        }
    }
}

internal class CommandUsage5<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val arg3: Argument<Sender, T3>,
    private val arg4: Argument<Sender, T4>,
    private val arg5: Argument<Sender, T5>,
    private val block: (Sender, T1, T2, T3, T4, T5) -> Unit
) : CommandUsage<Sender> {

    override val arguments = listOf(arg1, arg2, arg3, arg4, arg5)

    override fun execute(sender: Sender, args: Array<String>) {
        with(ExecutionContext(args)) {
            block(
                sender,
                get(sender, arg1),
                get(sender, arg2),
                get(sender, arg3),
                get(sender, arg4),
                getLast(sender, arg5)
            )
        }
    }
}

@Suppress("LongParameterList")
internal class CommandUsage6<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val arg3: Argument<Sender, T3>,
    private val arg4: Argument<Sender, T4>,
    private val arg5: Argument<Sender, T5>,
    private val arg6: Argument<Sender, T6>,
    private val block: (Sender, T1, T2, T3, T4, T5, T6) -> Unit
) : CommandUsage<Sender> {

    override val arguments = listOf(arg1, arg2, arg3, arg4, arg5, arg6)

    override fun execute(sender: Sender, args: Array<String>) {
        with(ExecutionContext(args)) {
            block(
                sender,
                get(sender, arg1),
                get(sender, arg2),
                get(sender, arg3),
                get(sender, arg4),
                get(sender, arg5),
                getLast(sender, arg6)
            )
        }
    }
}
