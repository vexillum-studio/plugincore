package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.*
import com.vexillum.plugincore.command.extractor.ExecutionContext
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.managers.language.LanguageAgent

interface Argument<Sender : LanguageAgent, Type : Any> {

    val processor: ArgumentProcessor<Sender, Type, Type>?

    val extractors: List<ArgumentExtractor<Sender, *>>

    fun get(sender: Sender, context: ExecutionContext): Type
}

abstract class Argument1<Sender : LanguageAgent, T1 : Any> : Argument<Sender, T1> {

    protected abstract val extractor: ArgumentExtractor<Sender, T1>

    override val extractors by lazy {
        listOf(extractor)
    }

    final override fun get(sender: Sender, context: ExecutionContext): T1 =
        extractor.extract(sender, context.nextArgument())
}

abstract class Argument2<Sender : LanguageAgent, T1 : Any, T2 : Any, Type : Any> : Argument<Sender, Type> {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>

    override val extractors by lazy {
        listOf(extractor1, extractor2)
    }

    protected abstract val merger: (Sender, T1, T2) -> Type

    final override fun get(sender: Sender, context: ExecutionContext): Type =
        try {
            merger(
                sender,
                extractor1.extract(sender, context.nextArgument()),
                extractor2.extract(sender, context.nextArgument())
            )
        } catch (e: Exception) {
            throw e
        }
}

abstract class Argument3<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, Type : Any> : Argument<Sender, Type> {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3)
    }

    protected abstract val merger: (Sender, T1, T2, T3) -> Type

    final override fun get(sender: Sender, context: ExecutionContext): Type =
        try {
            merger(
                sender,
                extractor1.extract(sender, context.nextArgument()),
                extractor2.extract(sender, context.nextArgument()),
                extractor3.extract(sender, context.nextArgument())
            )
        } catch (e: Exception) {
            throw e
        }
}

abstract class Argument4<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, Type : Any> : Argument<Sender, Type> {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>
    protected abstract val extractor4: ArgumentExtractor<Sender, T4>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3, extractor4)
    }

    protected abstract val merger: (Sender, T1, T2, T3, T4) -> Type

    final override fun get(sender: Sender, context: ExecutionContext): Type =
        try {
            merger(
                sender,
                extractor1.extract(sender, context.nextArgument()),
                extractor2.extract(sender, context.nextArgument()),
                extractor3.extract(sender, context.nextArgument()),
                extractor4.extract(sender, context.nextArgument())
            )
        } catch (e: Exception) {
            throw e
        }
}

abstract class Argument5<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, Type : Any> : Argument<Sender, Type> {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>
    protected abstract val extractor4: ArgumentExtractor<Sender, T4>
    protected abstract val extractor5: ArgumentExtractor<Sender, T5>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3, extractor4, extractor5)
    }

    protected abstract val merger: (Sender, T1, T2, T3, T4, T5) -> Type

    final override fun get(sender: Sender, context: ExecutionContext): Type =
        try {
            merger(
                sender,
                extractor1.extract(sender, context.nextArgument()),
                extractor2.extract(sender, context.nextArgument()),
                extractor3.extract(sender, context.nextArgument()),
                extractor4.extract(sender, context.nextArgument()),
                extractor5.extract(sender, context.nextArgument())
            )
        } catch (e: Exception) {
            throw e
        }
}

abstract class Argument6<Sender : LanguageAgent, T1 : Any, T2 : Any, T3 : Any, T4 : Any, T5 : Any, T6 : Any, Type : Any> : Argument<Sender, Type> {

    protected abstract val extractor1: ArgumentExtractor<Sender, T1>
    protected abstract val extractor2: ArgumentExtractor<Sender, T2>
    protected abstract val extractor3: ArgumentExtractor<Sender, T3>
    protected abstract val extractor4: ArgumentExtractor<Sender, T4>
    protected abstract val extractor5: ArgumentExtractor<Sender, T5>
    protected abstract val extractor6: ArgumentExtractor<Sender, T6>

    override val extractors by lazy {
        listOf(extractor1, extractor2, extractor3, extractor4, extractor5, extractor6)
    }

    protected abstract val merger: (Sender, T1, T2, T3, T4, T5, T6) -> Type

    final override fun get(sender: Sender, context: ExecutionContext): Type =
        try {
            merger(
                sender,
                extractor1.extract(sender, context.nextArgument()),
                extractor2.extract(sender, context.nextArgument()),
                extractor3.extract(sender, context.nextArgument()),
                extractor4.extract(sender, context.nextArgument()),
                extractor5.extract(sender, context.nextArgument()),
                extractor6.extract(sender, context.nextArgument())
            )
        } catch (e: Exception) {
            throw e
        }
}

abstract class ArgumentN<Sender : LanguageAgent, T1 : Any, Type : Any> : Argument<Sender, Type> {

    protected abstract val extractor: ArgumentExtractor<Sender, T1>

    override val extractors by lazy {
        listOf(extractor)
    }

    protected abstract val merger: (Sender, Iterator<T1>) -> Type

    final override fun get(sender: Sender, context: ExecutionContext): Type =
        try {
            merger(
                sender,
                iterator {
                    while (context.hasNextArgument()) {
                        yield(extractor.extract(sender, context.nextArgument()))
                    }
                }
            )
        } catch (e: Exception) {
            throw e
        }
}
