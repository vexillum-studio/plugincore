package com.vexillum.plugincore.language.node

import com.fasterxml.jackson.annotation.JsonValue
import com.vexillum.plugincore.language.LanguageDeserializer.LanguageDeserializerContext

internal class LanguageObject(
    override val address: Address,
    map: MutableMap<String, LanguageNode> = mutableMapOf()
) : LanguageNode(), MutableMap<String, LanguageNode> by map {

    override fun scopeResolver(
        context: LanguageDeserializerContext,
        key: String,
        visitedScope: VisitedScope
    ): LanguageResolver? =
        // Searches for absolute, root based property and defaults to relative property
        super.scopeResolver(context, key, visitedScope) ?: context.fromAddress(Address.of(key, address))

    @JsonValue
    override fun serialize() =
        toMap().mapValues { (_, node) -> node.serialize() }

    override fun toString() = toMap().toString()
}
