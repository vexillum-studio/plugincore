package com.vexillum.plugincore.extensions

import java.io.File
import java.io.InputStream

fun InputStream.copyTo(file: File) {
    use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}