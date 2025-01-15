package com.vexillum.plugincore.extensions

fun <T, R> Iterable<T>.flatMapNotNull(transform: (T) -> Iterable<R?>?): List<R> =
    flatMap { element ->
        transform(element)?.filterNotNull() ?: emptyList()
    }
