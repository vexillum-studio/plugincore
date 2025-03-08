package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.language.CompoundMessage
import com.vexillum.plugincore.language.Message
import com.vexillum.plugincore.language.MessageBlock
import com.vexillum.plugincore.language.ParameterBlock
import com.vexillum.plugincore.language.ReplacedBlock
import com.vexillum.plugincore.language.StartBlock

internal interface MessageHelper {

    fun param(key: String): ParameterBlock =
        ParameterBlock("{$key}")

    fun msg(value: String): MessageBlock =
        MessageBlock(value)

    fun repl(key: String, replaced: String): ReplacedBlock =
        ReplacedBlock(key, replaced)

    fun compound(vararg messages: Message) =
        CompoundMessage(arrayOf(*messages))

    fun message(vararg parts: Any): Message =
        parts.fold(StartBlock as Message) { acc, part ->
            when (part) {
                is Message -> acc + part
                else -> acc + msg(part.toString())
            }
        }
}
