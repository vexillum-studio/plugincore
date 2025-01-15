package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.argument.Argument
import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.managers.language.Console
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.util.Constants.SPACE

typealias ContextWrapper<Sender> =
    ExecutionContext<Sender>.(ExecutionContext<Sender>.() -> Any) -> ExecutionContext<Sender>

interface CommandUsage<Sender : LanguageAgent> {

    val arguments: List<Argument<Sender, *>>

    fun validate(sender: Sender, session: CommandSession): ExecutionContext<Sender>

    fun execute(sender: Sender, session: CommandSession)

    fun describe(agent: LanguageAgent): String =
        pluginCoreInstance.withAgent(agent) {
            arguments
                .asSequence()
                .map { it.describe(this) }
                .joinToString(
                    separator = SPACE
                )
        }
}

internal abstract class BaseCommandUsage<Sender : LanguageAgent> : CommandUsage<Sender> {

    override fun toString() =
        describe(Console)
}

internal class CommandUsage0<Sender : LanguageAgent>(
    val block: (Sender) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = emptyList<Argument<Sender, *>>()

    override fun validate(sender: Sender, session: CommandSession) =
        ExecutionContext<Sender>(session)

    override fun execute(sender: Sender, session: CommandSession) {
        block(sender)
    }
}

internal class CommandUsage1<Sender : LanguageAgent, T1 : Any>(
    private val arg1: Argument<Sender, T1>,
    val block: (Sender, T1) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1)

    override fun validate(sender: Sender, session: CommandSession) =
        executeWithContext(sender, session, { safeApply(it) })

    override fun execute(sender: Sender, session: CommandSession) {
        executeWithContext(sender, session) { a1 ->
            block(sender, a1)
        }
    }

    private fun executeWithContext(
        sender: Sender,
        session: CommandSession,
        contextWrapper: ContextWrapper<Sender> = { apply { it() } },
        action: ExecutionContext<Sender>.(T1) -> Unit = { _ -> }
    ): ExecutionContext<Sender> =
        ExecutionContext<Sender>(session).contextWrapper {
            action(
                getLast(sender, arg1)
            )
        }
}

internal class CommandUsage2<Sender : LanguageAgent, T1 : Any, T2 : Any>(
    private val arg1: Argument<Sender, T1>,
    private val arg2: Argument<Sender, T2>,
    private val block: (Sender, T1, T2) -> Unit
) : BaseCommandUsage<Sender>() {

    override val arguments = listOf(arg1, arg2)

    override fun validate(sender: Sender, session: CommandSession) =
        executeWithContext(sender, session, { safeApply(it) })

    override fun execute(sender: Sender, session: CommandSession) {
        executeWithContext(sender, session) { a1, a2 ->
            block(sender, a1, a2)
        }
    }

    private fun executeWithContext(
        sender: Sender,
        session: CommandSession,
        contextWrapper: ContextWrapper<Sender> = { apply { it() } },
        action: ExecutionContext<Sender>.(T1, T2) -> Unit = { _, _ -> }
    ): ExecutionContext<Sender> =
        ExecutionContext<Sender>(session).contextWrapper {
            action(
                get(sender, arg1),
                getLast(sender, arg2)
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

    override fun validate(sender: Sender, session: CommandSession) =
        executeWithContext(sender, session, { safeApply(it) })

    override fun execute(sender: Sender, session: CommandSession) {
        executeWithContext(sender, session) { a1, a2, a3 ->
            block(sender, a1, a2, a3)
        }
    }

    private fun executeWithContext(
        sender: Sender,
        session: CommandSession,
        contextWrapper: ContextWrapper<Sender> = { apply { it() } },
        action: ExecutionContext<Sender>.(T1, T2, T3) -> Unit = { _, _, _ -> }
    ): ExecutionContext<Sender> =
        ExecutionContext<Sender>(session).contextWrapper {
            action(
                get(sender, arg1),
                get(sender, arg2),
                getLast(sender, arg3)
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

    override fun validate(sender: Sender, session: CommandSession) =
        executeWithContext(sender, session, { safeApply(it) })

    override fun execute(sender: Sender, session: CommandSession) {
        executeWithContext(sender, session) { a1, a2, a3, a4 ->
            block(sender, a1, a2, a3, a4)
        }
    }

    private fun executeWithContext(
        sender: Sender,
        session: CommandSession,
        contextWrapper: ContextWrapper<Sender> = { apply { it() } },
        action: ExecutionContext<Sender>.(T1, T2, T3, T4) -> Unit = { _, _, _, _ -> }
    ): ExecutionContext<Sender> =
        ExecutionContext<Sender>(session).contextWrapper {
            action(
                get(sender, arg1),
                get(sender, arg2),
                get(sender, arg3),
                getLast(sender, arg4)
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

    override fun validate(sender: Sender, session: CommandSession) =
        executeWithContext(sender, session, { safeApply(it) })

    override fun execute(sender: Sender, session: CommandSession) {
        executeWithContext(sender, session) { a1, a2, a3, a4, a5 ->
            block(sender, a1, a2, a3, a4, a5)
        }
    }

    private fun executeWithContext(
        sender: Sender,
        session: CommandSession,
        contextWrapper: ContextWrapper<Sender> = { apply { it() } },
        action: ExecutionContext<Sender>.(T1, T2, T3, T4, T5) -> Unit = { _, _, _, _, _ -> }
    ): ExecutionContext<Sender> =
        ExecutionContext<Sender>(session).contextWrapper {
            action(
                get(sender, arg1),
                get(sender, arg2),
                get(sender, arg3),
                get(sender, arg4),
                getLast(sender, arg5)
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

    override fun validate(sender: Sender, session: CommandSession) =
        executeWithContext(sender, session, { safeApply(it) })

    override fun execute(sender: Sender, session: CommandSession) {
        executeWithContext(sender, session) { a1, a2, a3, a4, a5, a6 ->
            block(sender, a1, a2, a3, a4, a5, a6)
        }
    }

    private fun executeWithContext(
        sender: Sender,
        session: CommandSession,
        contextWrapper: ContextWrapper<Sender> = { apply { it() } },
        action: ExecutionContext<Sender>.(T1, T2, T3, T4, T5, T6) -> Unit = { _, _, _, _, _, _ -> }
    ): ExecutionContext<Sender> =
        ExecutionContext<Sender>(session).contextWrapper {
            action(
                get(sender, arg1),
                get(sender, arg2),
                get(sender, arg3),
                get(sender, arg4),
                get(sender, arg5),
                getLast(sender, arg6)
            )
        }
}
