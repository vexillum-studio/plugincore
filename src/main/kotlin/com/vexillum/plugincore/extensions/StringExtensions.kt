package com.vexillum.plugincore.extensions

fun String.trimEdges() =
    if (length <= 2) ""
    else substring(1, length - 1)

fun String.replaceInner(replacement: String) =
    replaceRange(1, lastIndex, replacement)
