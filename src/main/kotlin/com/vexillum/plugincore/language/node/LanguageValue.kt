package com.vexillum.plugincore.language.node

internal class LanguageValue(
    override val parent: LanguageContainer,
    override val id: Int,
    override val value: String
) : LanguageIdentity() {

    override fun equals(other: Any?): Boolean =
        (other as? LanguageValue)?.id == id

    override fun hashCode(): Int = id

    override fun toString() = value
}
