package com.vexillum.plugincore.language.node

internal class LanguageValue(
    override val address: Address,
    override val value: String
) : LanguageResolver() {

    override fun toString() = "$address=$value"
}
