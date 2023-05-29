package com.vexillum.plugincore.extensions

fun String.trimEdges() =
    substring(1, length - 1)

fun String.replaceInner(replacement: String) =
    replaceRange(1, lastIndex, replacement)
