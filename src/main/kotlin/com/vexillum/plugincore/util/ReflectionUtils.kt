package com.vexillum.plugincore.util

@Suppress("UNCHECKED_CAST")
fun <T> Any.fieldValue(fieldName: String): T {
    val field = javaClass.getDeclaredField(fieldName)
    field.isAccessible = true
    return field[this] as T
}
