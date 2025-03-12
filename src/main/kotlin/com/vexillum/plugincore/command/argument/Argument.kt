package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.ExecutionContext
import com.vexillum.plugincore.command.extractor.ArgumentExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.buildMessage
import com.vexillum.plugincore.util.Constants.SPACE

interface Argument<Sender : LanguageAgent, Type : Any> {

    val slots: Int

    val processor: ArgumentProcessor<Sender, Type, Type>?

    val extractors: List<ArgumentExtractor<Sender, *>>

    fun get(context: ExecutionContext<Sender>): Type

    fun describe(user: CommandUser<*>): Message =
        buildMessage {
            extractors.joinMessage(SPACE) { describe(user) }
        }
}

abstract class BaseArgument<Sender : LanguageAgent, Type : Any> : Argument<Sender, Type> {

    override val slots get() = extractors.count()
}

abstract class Argument1<Sender : LanguageAgent, T1 : Any> :
    BaseArgument<Sender, T1>() {

    protected abstract val extractor: ArgumentExtractor<Sender, T1>

    override val extractors by lazy {
        listOf(extractor)
    }

    final override fun get(context: ExecutionContext<Sender>): T1 =
        with(context) {
            extract(extractor)
        }
}

abstract class Argument2<Sender : LanguageAgent, T1 : Any, T2 : Any, Type : Any> :
    BaseArgument<Sender, Type>() {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>

    override val extractors by lazy {
        listOf(extractor1, extractor2)
    }

    protected abstract val merger: (CommandUser<Sender>, T1, T2) -> Type

    final override fun get(context: ExecutionContext<Sender>): Type =
        with(context) {
            merger(
                this,
                extract(extractor1),
                extract(extractor2)
            )
        }
}

abstract class Argument3<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, Type : Any> :
    BaseArgument<Sender, Type>() {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3)
    }

    protected abstract val merger: (CommandUser<Sender>, T1, T2, T3) -> Type

    final override fun get(context: ExecutionContext<Sender>): Type =
        with(context) {
            merger(
                this,
                extract(extractor1),
                extract(extractor2),
                extract(extractor3)
            )
        }
}

abstract class Argument4<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, Type : Any> :
    BaseArgument<Sender, Type>() {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>
    protected abstract val extractor4: ArgumentExtractor<Sender, T4>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3, extractor4)
    }

    protected abstract val merger: (CommandUser<Sender>, T1, T2, T3, T4) -> Type

    final override fun get(context: ExecutionContext<Sender>): Type =
        with(context) {
            merger(
                this,
                extract(extractor1),
                extract(extractor2),
                extract(extractor3),
                extract(extractor4)
            )
        }
}

abstract class Argument5<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, Type : Any> :
    BaseArgument<Sender, Type>() {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>
    protected abstract val extractor4: ArgumentExtractor<Sender, T4>
    protected abstract val extractor5: ArgumentExtractor<Sender, T5>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3, extractor4, extractor5)
    }

    protected abstract val merger: (CommandUser<Sender>, T1, T2, T3, T4, T5) -> Type

    final override fun get(context: ExecutionContext<Sender>): Type =
        with(context) {
            merger(
                this,
                extract(extractor1),
                extract(extractor2),
                extract(extractor3),
                extract(extractor4),
                extract(extractor5)
            )
        }
}

abstract class Argument6<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, Type : Any> :
    BaseArgument<Sender, Type>() {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>
    protected abstract val extractor4: ArgumentExtractor<Sender, T4>
    protected abstract val extractor5: ArgumentExtractor<Sender, T5>
    protected abstract val extractor6: ArgumentExtractor<Sender, T6>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3, extractor4, extractor5, extractor6)
    }

    protected abstract val merger: (CommandUser<Sender>, T1, T2, T3, T4, T5, T6) -> Type

    final override fun get(context: ExecutionContext<Sender>): Type =
        with(context) {
            merger(
                this,
                extract(extractor1),
                extract(extractor2),
                extract(extractor3),
                extract(extractor4),
                extract(extractor5),
                extract(extractor6)
            )
        }
}

abstract class ArgumentN<Sender : LanguageAgent, T1 : Any, Type : Any>(
    final override val slots: Int
) : BaseArgument<Sender, Type>() {

    protected abstract val extractor: ArgumentExtractor<Sender, T1>

    override val extractors by lazy {
        listOf(extractor)
    }

    protected abstract val merger: (CommandUser<Sender>, Iterator<T1>) -> Type

    final override fun get(context: ExecutionContext<Sender>): Type =
        with(context) {
            merger(
                this,
                iterator {
                    while (hasNextArgument()) {
                        yield(extract(extractor))
                    }
                }
            )
        }
}

abstract class ArgumentInfinite<Sender : LanguageAgent, T1 : Any, Type : Any> :
    BaseArgument<Sender, Type>() {

    final override val slots: Int = Int.MAX_VALUE

    protected abstract val extractor: ArgumentExtractor<Sender, T1>

    final override val extractors by lazy {
        listOf(extractor)
    }

    protected abstract val merger: (CommandUser<Sender>, Iterator<T1>) -> Type

    final override fun get(context: ExecutionContext<Sender>): Type =
        with(context) {
            merger(
                this,
                iterator {
                    while (hasNextArgument()) {
                        yield(extract(extractor))
                    }
                }
            )
        }
}

abstract class PlainArgument<Sender : LanguageAgent, T1 : Any, Type : Any> :
    BaseArgument<Sender, Type>() {

    final override val slots: Int = Int.MAX_VALUE

    final override val extractors by lazy { listOf(extractor) }

    protected abstract val extractor: ArgumentExtractor<Sender, Type>

    final override fun get(context: ExecutionContext<Sender>): Type =
        extractor.extract(context, context.capturedInput)
}
