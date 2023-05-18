package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.languageFromJson
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class LanguageTests {

    data class ExampleLanguage(
        val key1: Message,
        val key2: Message,
        val key3: Message,
        val key4: Message,
        val key5: Message
    )

    private lateinit var language: Language<ExampleLanguage>

    @BeforeEach
    fun setUp() {
        val exampleLanguageJson = """
            {
            "color1":"&1",
            "constant":"there",
            "key1":"Hello",
            "key2":"Hello {replacement}",
            "key3":"Hello {constant}",
            "key4":"-{key3} -General {generalName}",
            "key5":"&4{key1}"
            }
        """.trimIndent()

        language = languageFromJson(exampleLanguageJson)
    }

    @Test
    fun `should resolve simple values`() {
        assertThat(language.resolve { key1 }, `is`("Hello"))
    }

    @Test
    fun `should resolve simple replacements`() {
        assertThat(language.resolve(mapOf("replacement" to "World!")) { key2 }, `is`("Hello World!"))
    }

    @Test
    fun `should resolve constants not defined in the language schema`() {
        assertThat(language.resolve { key3 }, `is`("Hello there"))
    }

    @Test
    fun `should resolve nested message replacements with replace map`() {
        assertThat(language.resolve(mapOf("generalName" to "Kenobi")) { key4 }, `is`("-Hello there -General Kenobi"))
    }

    @Test
    fun `should resolve nested message replacements with colors`() {
        assertThat(language.resolve { key5 }, `is`("§4Hello"))
    }
}
