package com.vexillum.plugincore.managers.language.node

import com.fasterxml.jackson.annotation.JsonValue

internal class LanguageArray(
    override val parent: LanguageContainer,
    override val id: Int,
    collection: MutableList<LanguageValue> = mutableListOf()
) : LanguageIdentity(), MutableCollection<LanguageValue> by collection {

    override val value get() = joinToString(separator = "\n")

    override fun toString() = toList().toString()

    @Suppress("unused")
    @JsonValue
    fun serialize() =
        LanguageValue(parent, id, value)
}
