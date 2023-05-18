package com.vexillum.plugincore.managers.language

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonValue

data class Message @JsonCreator constructor(
    @get:JsonValue val raw: String
)
