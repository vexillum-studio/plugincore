package com.vexillum.plugincore.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import java.time.format.DateTimeFormatter

class DateTimeModule : SimpleModule() {

    init {
        this.addDeserializer(DateTimeFormatter::class.java, DateTimeFormatterDeserializer)
    }
}

object DateTimeFormatterDeserializer : JsonDeserializer<DateTimeFormatter>() {

    override fun deserialize(
        parser: JsonParser,
        context: DeserializationContext?
    ): DateTimeFormatter {
        val pattern = parser.text
        return DateTimeFormatter.ofPattern(pattern)
    }
}
