package com.vexillum.plugincore.util

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinModule

object JsonUtil {

    const val JSON_EXTENSION = ".json"

    val mapper: ObjectMapper = JsonMapper.builder()
        .enable(ACCEPT_CASE_INSENSITIVE_ENUMS)
        .build()
        .registerModule(KotlinModule.Builder().build())
        .registerModule(JavaTimeModule())

    fun toJsonNode(value: Any): JsonNode = mapper.valueToTree(value)

    inline fun <reified T> fromJsonNode(jsonNode: JsonNode): T =
        mapper.treeToValue(jsonNode, T::class.java)

    inline fun <reified T> fromJson(json: String): T =
        mapper.readValue(json, T::class.java)

    fun <T> fromJson(json: String, clazz: Class<T>): T =
        mapper.readValue(json, clazz)

    inline fun <reified T> convert(value: Any): T =
        mapper.convertValue(value, T::class.java)
}
