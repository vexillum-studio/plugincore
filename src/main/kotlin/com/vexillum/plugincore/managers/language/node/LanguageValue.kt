package com.vexillum.plugincore.managers.language.node

internal class LanguageValue(
    override val parent: LanguageContainer,
    override val id: Int,
    override val value: String
) : LanguageIdentity() {

    override fun toString() = value
}
