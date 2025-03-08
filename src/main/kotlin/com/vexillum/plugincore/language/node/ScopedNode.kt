package com.vexillum.plugincore.language.node

internal abstract class ScopedNode : LanguageNode {

    open fun scopeProperty(key: String, visitedScope: VisitedScope): LanguageIdentity? {
        val pointer = pointer(key)
        return parent(pointer, visitedScope)
    }

    private fun parent(pointer: List<String>, visitedScope: VisitedScope): LanguageIdentity? {
        var currentParent = parent
        while (currentParent != null) {
            val result = currentParent.child(pointer, visitedScope)
            if (result != null) {
                return result
            }
            currentParent = currentParent.parent
        }
        return null
    }

    protected fun pointer(key: String) =
        key.split(".")

    class VisitedScope {

        private val visited = mutableSetOf<ScopedNode>()

        fun add(node: ScopedNode) {
            if (visited.contains(node)) {
                error("Invalid cyclic dependency in keys: $visited")
            }
            visited.add(node)
        }
    }
}
