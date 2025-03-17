package com.vexillum.plugincore.language.node

import com.fasterxml.jackson.annotation.JsonValue
import com.vexillum.plugincore.language.deserializer.MessageDeserializerContext

internal abstract class LanguageNode {
    abstract val address: Address

    open fun scopeResolver(
        context: MessageDeserializerContext,
        key: String,
        visitedScope: VisitedScope
    ): LanguageResolver? {
        var currentAddress: Address? = if (this is LanguageResolver) address.parent else address
        while (currentAddress != null) {
            val resolver = context.fromAddress(currentAddress.child(key))
            if (resolver != null) {
                return resolver
            }
            currentAddress = currentAddress.parent
        }
        return null
    }

    @JsonValue
    open fun serialize(): Any =
        address

    override fun hashCode(): Int = address.hashCode()

    override fun equals(other: Any?) =
        (other as? LanguageNode)?.address == address

    class VisitedScope {

        private val visited = mutableSetOf<LanguageNode>()

        operator fun plusAssign(node: LanguageNode) {
            if (node in this) {
                val visitedAddresses = visited.map { visitedNode ->
                    visitedNode.address
                }
                error("Invalid cyclic dependency in keys: $visitedAddresses")
            }
            visited.add(node)
        }

        operator fun contains(node: LanguageNode) =
            visited.contains(node)
    }
}
