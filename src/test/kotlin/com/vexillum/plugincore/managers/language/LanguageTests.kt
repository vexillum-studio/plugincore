@file:Suppress("SpellCheckingInspection")

package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.assertThrowsMessageContaining
import com.vexillum.plugincore.language.InvalidLanguageException
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
        val descriptor: Descriptor
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

    data class Descriptor(
        val message: Message,
        val message2: Message,
        val x: Message,
        val y: Message,
        val z: Message
    )

    private lateinit var localeTranslation: LocaleTranslation<ExampleLanguage>

    data class TestLanguage(
        val object1: Object1,
        val object2: Object2
    ) : Language

    data class Object1(
        val key1: Message,
        val key2: Message,
        val key3: Message,
        val key4: Message
    )

    data class Object2(
        val key5: Message,
        val key6: Message,
    )

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
                    "message": "This describes {descriptor}",
                    "message2": "This goes to {key6}",
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

    @Test
    fun `should resolve with parameter when the key points to it's container or an object`() {
        // descriptor.message is using argument {descriptor} wich points to its own parent
        assertResolve("This describes something") {
            descriptor.message.replace("descriptor", "something")
        }
        // Points to an external object key, treated as parameter
        assertResolve("This goes to the moon!") {
            descriptor.message2.replace("key6", "the moon!")
        }
    }

    @Test
    fun `should get missing language key error`() {
        assertThrowsMessageContaining<InvalidLanguageException>(
            "\"key2\": <--- Missing field in json, language class Object1 is expecting this field"
        ) {
            languageFromJson<TestLanguage>(
                """
                {
                   "object1": {
                      "key1": "This is key1",
                      "key3": "This is key3",
                      "key4": "This is key4"
                   },
                   "object2": {
                      "key5": "This is key5}",
                      "key6": "This is key6"
                   }
                }
                """.trimIndent()
            )
        }
    }

    @Test
    fun `should get cyclic dependency error`() {
        assertThrowsMessageContaining<InvalidLanguageException>(
            "Invalid cyclic dependency in keys: [object1.key1, object1.key2, object1.key3]"
        ) {
            languageFromJson<TestLanguage>(
                """
                {
                   "object1": {
                      "key1": "This starts a cyclic dependency {key2}",
                      "key2": "{key3}",
                      "key3": "{key2}",
                      "key4": "{key5}"
                   },
                   "object2": {
                      "key5": "This creates a cyclic dependency with other object {key6}",
                      "key6": "{key4}"
                   }
                }
                """.trimIndent()
            )
        }
        assertThrowsMessageContaining<InvalidLanguageException>(
            "Invalid cyclic dependency in keys: [object1.key4, object2.key5, object2.key6]"
        ) {
            languageFromJson<TestLanguage>(
                """
                {
                   "object1": {
                      "key1": "Normal message",
                      "key2": "Nothing unusual",
                      "key3": "Yep, still normal",
                      "key4": "{object2.key5}"
                   },
                   "object2": {
                      "key5": "This creates a cyclic dependency with other object {key6}",
                      "key6": "{object1.key4}"
                   }
                }
                """.trimIndent()
            )
        }
    }

    private fun assertResolve(
        expected: String,
        block: ExampleLanguage.() -> Message
    ) {
        assertThat(localeTranslation.resolve(block).resolved(), `is`(expected))
    }
}
