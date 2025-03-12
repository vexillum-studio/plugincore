@file:Suppress("SpellCheckingInspection")

package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LocaleTranslation
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageList
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
        val key4: MessageList,
        val key5: Message,
        val key6: KeySix,
        val key7: MessageList,
        val key8: Message,
    ) : Language

    data class KeySix(
        val nested: Message,
        val nestedComplex: Message,
        val nestedObject: NestedObject,
    )

    data class NestedObject(
        val `object`: Message,
        val newObject: Message
    )

    private lateinit var localeTranslation: LocaleTranslation<ExampleLanguage>

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
                    "nestedComplex": "{color1}A chest is located at {descriptor.x} {x} {descriptor.y} {y} {descriptor.z} {z}",
                    "nestedObject": {
                       "object": "{color1}Hello",
                       "newObject": "Descriptor is {descriptor.x} and Nested is {nested}"
                    }
                },
                "key7": [
                    "7.1",
                    "7.2",
                    "7.3",
                    "7.4",
                    "7.5",
                    "{key6.nested} 7.6"
                ],
                "key8": "{color1}{key1} {key6.nested} 7.6 {key6.nestedComplex} 7.6 ",
                "descriptor": {
                    "x": "x",
                    "y": "y",
                    "z": "z"
                }
            }
        """.trimIndent()
        localeTranslation = languageFromJson(exampleLanguageJson)
    }

    @Test
    fun `should resolve simple values`() {
        assertResolve("Hello") { key1 }
    }

    @Test
    fun `should resolve simple replacements`() {
        assertResolve("Hello World!") { key2.replace("replacement", "World!") }
    }

    @Test
    fun `should resolve constants not defined in the language schema`() {
        assertResolve("Hello there") { key3 }
    }

    @Test
    fun `should resolve nested message replacements with replace map`() {
        assertResolve("-Hello there\n-General Kenobi") {
            key4.replace("generalName", "Kenobi")
        }
    }

    @Test
    fun `should resolve nested message replacements with colors`() {
        assertResolve("§4Hello") { key5 }
    }

    @Test
    fun `should resolve list messages by index`() {
        assertResolve("7.1") { key7[0] }
        assertResolve("7.2") { key7[1] }
        assertResolve("7.3") { key7[2] }
        assertResolve("7.4") { key7[3] }
        assertResolve("7.5") { key7[4] }
    }

    @Test
    fun `should resolve with scope inside list messages`() {
        assertResolve("nestedValue 7.6") { key7.last() }
    }

    @Test
    fun `should resolve various messages without replacements`() {
        assertResolve("§1Hello nestedValue 7.6 §1A chest is located at x {x} y {y} z {z} 7.6 ") { key8 }
    }

    @Test
    fun `should resolve simple message and nested object`() {
        assertResolve("§1Hello") { key6.nestedObject.`object` }
        assertResolve("Descriptor is x and Nested is nestedValue") { key6.nestedObject.newObject }
    }

    @Test
    fun `should resolve and strip color from messages`() {
        assertThat(localeTranslation.resolve { key1 }.stripped(), `is`("Hello"))
    }

    @Test
    fun `should resolve and replace overriding replacements`() {
        val resolvedMessage = localeTranslation.resolve { key6.nestedComplex }
            .replacing(
                "color1" to "COLOR1",
                "descriptor.x" to "X-coordinate",
                "descriptor.y" to "Y-coordinate",
                "descriptor.z" to "Z-coordinate",
                "x" to 100,
                "y" to 200,
                "z" to 300
            )
        assertThat(
            resolvedMessage,
            `is`("COLOR1A chest is located at X-coordinate 100 Y-coordinate 200 Z-coordinate 300")
        )
    }

    private fun assertResolve(
        expected: String,
        block: ExampleLanguage.() -> Message
    ) {
        assertThat(localeTranslation.resolve(block).resolved(), `is`(expected))
    }
}
