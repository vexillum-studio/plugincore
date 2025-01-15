package com.vexillum.plugincore.extensions

fun <T, C : Collection<T>, R> C.letIfNotEmpty(block: (C) -> R): R? =
    if (isNotEmpty()) {
        block(this)
    } else {
        null
    }
