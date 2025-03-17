package com.vexillum.plugincore.language.message

import com.vexillum.plugincore.language.message.EmptyBlock.messageOf

class MutateMessageBlock {

    private var default: (Message) -> Message = { message -> message }

    private var message: (MessageBlock) -> Message = default

    private var parameter: (ParameterBlock) -> Message = default

    private var replacement: (ReplacedBlock) -> Message = default

    private var list: (MessageList) -> Message = { list ->
        MessageList(list.messages.map { mutate(it) })
    }

    fun default(block: (Message) -> Message) {
        default = block
    }

    fun message(block: (MessageBlock) -> Message) {
        message = block
    }

    fun parameter(block: (ParameterBlock) -> Message) {
        parameter = block
    }

    fun replacement(block: (ReplacedBlock) -> Message) {
        replacement = block
    }

    fun list(block: (MessageList) -> Message) {
        list = block
    }

    fun mutate(message: Message): Message =
        when (message) {
            is MessageBlock -> message(message)
            is ParameterBlock -> parameter(message)
            is ReplacedBlock -> replacement(message)
            is MessageList -> list(message)
            is CompoundMessage -> messageOf(message.messages.map(this::mutate))
            is MessageWithReplacements -> MessageWithReplacements(
                message = mutate(message.message),
                messageReplacements = message.messageReplacements
            )

            else -> default(message)
        }
}
