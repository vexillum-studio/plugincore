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
                "color1": "&1",
                "constant": "there",
                "key1": "Hello",
                "key2": "Hello {replacement}",
                "key3": "Hello {constant}",
                "key4": [
                    "-{key3}",
                    "-General {generalName}"
                ],
                "key5": "&4{key1}",
                "key6": {
                    "nested": "nestedValue",
                    "nestedComplex": "A chest is located at {descriptor.x} {x} {descriptor.y} {y} {descriptor.z} {z}"
                },
                "descriptor": {
                    "x": "x",
                    "y": "y",
                    "z": "z"
                }
            }
        """.trimIndent()

        language = languageFromJson(exampleLanguageJson)
    }

    @Test
    fun `should resolve simple values`() {
        assertResolve("Hello") { key1 }
    }

    @Test
    fun `should resolve simple replacements`() {
        assertResolve(
            "Hello World!",
            mapOf("replacement" to "World!")
        ) { key2 }
    }

    @Test
    fun `should resolve constants not defined in the language schema`() {
        assertResolve("Hello there") { key3 }
    }

    @Test
    fun `should resolve nested message replacements with replace map`() {
        /*assertResolve(
            "-Hello there\n-General Kenobi",
            mapOf("generalName" to "Kenobi")
        ) { key4 }*/
    }

    @Test
    fun `should resolve nested message replacements with colors`() {
        assertResolve("§4Hello") { key5 }
    }

    private fun assertResolve(
        expected: String,
        replacements: Map<String, Any> = emptyMap(),
        block: ExampleLanguage.() -> Message
    ) {
        assertThat(language.resolve(replacements, block), `is`(expected))
    }

}
