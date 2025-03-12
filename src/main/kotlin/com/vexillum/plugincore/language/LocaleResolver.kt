package com.vexillum.plugincore.language

import com.vexillum.plugincore.language.message.Message

interface LocaleResolver<T : Language> {

    val value: T

    fun <M : Message> resolve(
        block: T.() -> M
    ): M =
        value.run(block)

    fun <S, R> resolving(
        selector: T.() -> S,
        block: Scope<S>.() -> R
    ): R =
        object : Scope<S> {
            override val value = this@LocaleResolver.value.run(selector)
        }.run(block)

    interface Scope<S> {
        val value: S
        fun <M : Message> resolve(
            block: S.() -> M
        ): M =
            value.run(block)
    }
}
