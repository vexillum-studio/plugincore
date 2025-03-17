package com.vexillum.plugincore.language.message

interface MessageReplacements : Map<String, Any> {
    val replacements: Map<String, Any>
    fun replace(key: String, value: Any)
    fun replace(vararg replacements: Pair<String, Any>)
    fun replaceAll(map: Map<String, Any>)
    operator fun plus(other: MessageReplacements): MessageReplacements
}

internal fun messageReplacements(
    initialReplacements: Map<String, Any> = mutableMapOf()
): MessageReplacements =
    MessageReplacementsImpl(initialReplacements.toMutableMap())

private class MessageReplacementsImpl(
    private val innerReplacements: MutableMap<String, Any>
) : MessageReplacements, Map<String, Any> by innerReplacements {

    override val replacements: Map<String, Any> get() = innerReplacements

    override fun replace(key: String, value: Any) {
        innerReplacements[key] = value
    }

    override fun replace(vararg replacements: Pair<String, Any>) {
        replacements.forEach { (key, value) ->
            innerReplacements[key] = value
        }
    }

    override fun replaceAll(map: Map<String, Any>) {
        innerReplacements.putAll(map)
    }

    override fun plus(other: MessageReplacements): MessageReplacements {
        innerReplacements += other.replacements
        return this
    }
}
