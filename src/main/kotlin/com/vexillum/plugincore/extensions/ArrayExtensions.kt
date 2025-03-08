package com.vexillum.plugincore.extensions

inline fun <reified T : Any> Array<T>.repeat(times: Int): Array<T> =
    Array(size * times) { this[it % size] }

@Suppress("SpreadOperator")
inline operator fun <reified T : Any> T.plus(array: Array<T>): Array<T> =
    arrayOf(this, *array)

inline fun <reified T : Any> Array<T>.replaceFirst(newValue: T): Array<T> =
    Array(size) { i ->
        if (i == 0) newValue else this[i]
    }

inline fun <reified T : Any> Array<T>.replaceLast(newValue: T): Array<T> =
    Array(size) { i ->
        if (i == lastIndex) newValue else this[i]
    }

inline fun <reified T : Any> Array<T>.combineEdges(other: Array<T>, mergedValue: T): Array<T> =
    if (this.isEmpty()) other
    else if (other.isEmpty()) this
    else
        Array(size + other.size - 1) { i ->
            if (i < lastIndex) this[i]
            else if (i == lastIndex) mergedValue
            else other[i - lastIndex]
        }
