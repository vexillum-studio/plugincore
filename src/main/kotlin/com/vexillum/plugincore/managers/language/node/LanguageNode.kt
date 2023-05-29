package com.vexillum.plugincore.managers.language.node

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties("parent", "value")
internal sealed interface LanguageNode {
    val parent: LanguageContainer?
}
