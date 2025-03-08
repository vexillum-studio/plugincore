package com.vexillum.plugincore.language.node

internal class LanguageObject(
    override val parent: LanguageContainer? = null,
    map: MutableMap<String, ScopedNode> = mutableMapOf()
) : LanguageContainer(), MutableMap<String, ScopedNode> by map {

    override fun child(pointer: List<String>, visitedScope: VisitedScope): LanguageIdentity? {
        val nextKey = pointer.first()
        val child = get(nextKey) ?: return null
        val nextKeys = pointer.drop(1)
        return if (nextKeys.isEmpty() && child is LanguageIdentity) {
            child
        } else if (child is LanguageContainer) {
            child.child(nextKeys, visitedScope)
        } else {
            null
        }
    }

    override fun toString() = toMap().toString()
}
