package com.vexillum.plugincore.extensions

fun <T> takeWhen(
    condition: Boolean,
    block: () -> T
): T? =
    if (condition) block() else null

@Suppress("UNCHECKED_CAST")
fun <T> Any?.tryCastOrNull(): T? =
    if (this as? T != null) this else null
