package com.vexillum.plugincore.language.message

interface MessageReplacements {
    val replacements: Map<String, Any>
    fun replace(key: String, value: Any)
    fun replace(vararg replacements: Pair<String, Any>)
    fun replaceAll(map: Map<String, Any>)
    operator fun plus(other: MessageReplacements): MessageReplacements
}

internal fun messageReplacements(): MessageReplacements =
    MessageReplacementsImpl()

private class MessageReplacementsImpl : MessageReplacements {

    private val _replacements = mutableMapOf<String, Any>()

    override val replacements: Map<String, Any> get() = _replacements

    override fun replace(key: String, value: Any) {
        _replacements[key] = value
    }

    override fun replace(vararg replacements: Pair<String, Any>) {
        replacements.forEach { (key, value) ->
            _replacements[key] = value
        }
    }

    override fun replaceAll(map: Map<String, Any>) {
        _replacements.putAll(map)
    }

    override fun plus(other: MessageReplacements): MessageReplacements {
        _replacements += other.replacements
        return this
    }
}
