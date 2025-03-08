package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.argument.Argument
import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.command.session.ConsoleUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.LanguageMessage
import com.vexillum.plugincore.language.buildMessage
import com.vexillum.plugincore.util.Constants.SPACE

interface CommandUsage<Sender : LanguageAgent> {

    val arguments: List<Argument<Sender, *>>

    fun validate(session: CommandSession<Sender>): ExecutionContext<Sender>

    fun execute(session: CommandSession<Sender>): ExecutionContext<Sender>

    fun describe(user: CommandUser<*>): LanguageMessage =
        buildMessage {
            arguments.joinMessage(separator = SPACE) {
                describe(user)
            }
        }
}

internal abstract class BaseCommandUsage<Sender : LanguageAgent> : CommandUsage<Sender> {

    override fun toString() =
        describe(ConsoleUser).toString()
}

internal class CommandUsage0<Sender : LanguageAgent>(
    val block: (Sender) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = emptyList<Argument<Sender, *>>()

    override fun validate(session: CommandSession<Sender>) =
        session.executionContext()

    override fun execute(session: CommandSession<Sender>) =
        session.executionContext().safeApply {
            block(session.agent)
        }
}

internal class CommandUsage1<Sender : LanguageAgent, T1 : Any>(
    private val arg1: Argument<Sender, T1>,
    val block: (Sender, T1) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1)

    override fun validate(session: CommandSession<Sender>) =
        executeWithContext(session)

    override fun execute(session: CommandSession<Sender>) =
        executeWithContext(session) { a1 ->
            block(agent, a1)
        }

    private fun executeWithContext(
        session: CommandSession<Sender>,
        action: ExecutionContext<Sender>.(T1) -> Unit = { _ -> }
    ): ExecutionContext<Sender> =
        session.executionContext().safeApply {
            action(
                getLast(arg1)
            )
        }
}

internal class CommandUsage2<Sender : LanguageAgent, T1 : Any, T2 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val block: (Sender, T1, T2) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1, arg2)

    override fun validate(session: CommandSession<Sender>) =
        executeWithContext(session)

    override fun execute(session: CommandSession<Sender>) =
        executeWithContext(session) { a1, a2 ->
            block(agent, a1, a2)
        }

    private fun executeWithContext(
        session: CommandSession<Sender>,
        action: ExecutionContext<Sender>.(T1, T2) -> Unit = { _, _ -> }
    ): ExecutionContext<Sender> =
        session.executionContext().safeApply {
            action(
                get(arg1),
                getLast(arg2)
            )
        }
}

internal class CommandUsage3<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val arg3: Argument<Sender, T3>,
    private val block: (Sender, T1, T2, T3) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1, arg2, arg3)

    override fun validate(session: CommandSession<Sender>) =
        executeWithContext(session)

    override fun execute(session: CommandSession<Sender>) =
        executeWithContext(session) { a1, a2, a3 ->
            block(agent, a1, a2, a3)
        }

    private fun executeWithContext(
        session: CommandSession<Sender>,
        action: ExecutionContext<Sender>.(T1, T2, T3) -> Unit = { _, _, _ -> }
    ): ExecutionContext<Sender> =
        session.executionContext().safeApply {
            action(
                get(arg1),
                get(arg2),
                getLast(arg3)
            )
        }
}

internal class CommandUsage4<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val arg3: Argument<Sender, T3>,
    private val arg4: Argument<Sender, T4>,
    private val block: (Sender, T1, T2, T3, T4) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1, arg2, arg3, arg4)

    override fun validate(session: CommandSession<Sender>) =
        executeWithContext(session)

    override fun execute(session: CommandSession<Sender>) =
        executeWithContext(session) { a1, a2, a3, a4 ->
            block(agent, a1, a2, a3, a4)
        }

    private fun executeWithContext(
        session: CommandSession<Sender>,
        action: ExecutionContext<Sender>.(T1, T2, T3, T4) -> Unit = { _, _, _, _ -> }
    ): ExecutionContext<Sender> =
        ExecutionContext(session).safeApply {
            action(
                get(arg1),
                get(arg2),
                get(arg3),
                getLast(arg4)
            )
        }
}

internal class CommandUsage5<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val arg3: Argument<Sender, T3>,
    private val arg4: Argument<Sender, T4>,
    private val arg5: Argument<Sender, T5>,
    private val block: (Sender, T1, T2, T3, T4, T5) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1, arg2, arg3, arg4, arg5)

    override fun validate(session: CommandSession<Sender>) =
        executeWithContext(session)

    override fun execute(session: CommandSession<Sender>) =
        executeWithContext(session) { a1, a2, a3, a4, a5 ->
            block(agent, a1, a2, a3, a4, a5)
        }

    private fun executeWithContext(
        session: CommandSession<Sender>,
        action: ExecutionContext<Sender>.(T1, T2, T3, T4, T5) -> Unit = { _, _, _, _, _ -> }
    ): ExecutionContext<Sender> =
        session.executionContext().safeApply {
            action(
                get(arg1),
                get(arg2),
                get(arg3),
                get(arg4),
                getLast(arg5)
            )
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
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1, arg2, arg3, arg4, arg5, arg6)

    override fun validate(session: CommandSession<Sender>) =
        executeWithContext(session)

    override fun execute(session: CommandSession<Sender>) =
        executeWithContext(session) { a1, a2, a3, a4, a5, a6 ->
            block(agent, a1, a2, a3, a4, a5, a6)
        }

    private fun executeWithContext(
        session: CommandSession<Sender>,
        action: ExecutionContext<Sender>.(T1, T2, T3, T4, T5, T6) -> Unit = { _, _, _, _, _, _ -> }
    ): ExecutionContext<Sender> =
        session.executionContext().safeApply {
            action(
                get(arg1),
                get(arg2),
                get(arg3),
                get(arg4),
                get(arg5),
                getLast(arg6)
            )
        }
}
