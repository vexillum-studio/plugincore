package com.vexillum.plugincore.managers.language.node

internal abstract class LanguageContainer : ScopedNode() {

    final override fun scopeProperty(key: String, visitedScope: VisitedScope): LanguageIdentity? {
        return super.scopeProperty(key, visitedScope) ?: child(pointer(key), visitedScope)
    }

    abstract fun child(pointer: List<String>, visitedScope: VisitedScope): LanguageIdentity?
}
