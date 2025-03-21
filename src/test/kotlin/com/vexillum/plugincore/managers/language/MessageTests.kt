package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.language.message.CompoundMessage
import com.vexillum.plugincore.language.message.MessageBlock
import com.vexillum.plugincore.language.message.MessageFactory
import com.vexillum.plugincore.language.message.ParameterBlock
import com.vexillum.plugincore.language.message.buildMessage
import com.vexillum.plugincore.language.message.message
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class MessageTests : MessageFactory {

    @Test
    fun `should merge messages accordingly by case`() {
        assertThat(
            msg("A") + msg("B"),
            `is`(MessageBlock("AB"))
        )
        assertThat(
            msg("Text with ") + param("parameter"),
            `is`(
                CompoundMessage(
                    arrayOf(
                        MessageBlock("Text with "),
                        ParameterBlock("{parameter}")
                    )
                )
            )
        )
        assertThat(
            param("B") + compound(msg("A")),
            `is`(
                CompoundMessage(
                    arrayOf(
                        ParameterBlock("{B}"),
                        MessageBlock("A")
                    )
                )
            )
        )
        assertThat(
            compound(msg("A")) + param("B"),
            `is`(
                CompoundMessage(
                    arrayOf(
                        MessageBlock("A"),
                        ParameterBlock("{B}")
                    )
                )
            )
        )
        val compound1 = compound(msg("A"), param("B"))
        val compound2 = compound(msg("C"), param("D"))
        assertThat(
            (compound1 + compound2),
            `is`(
                CompoundMessage(
                    arrayOf(
                        MessageBlock("A"),
                        ParameterBlock("{B}"),
                        MessageBlock("C"),
                        ParameterBlock("{D}")
                    )
                )
            )
        )
    }

    @Test
    fun `should merge language messages`() {
        val message = messageOf("This is a ", param("replace"), " case")
        val expectedMessage = CompoundMessage(
            arrayOf(
                MessageBlock("This is a "),
                ParameterBlock("{replace}"),
                MessageBlock(" case"),
            )
        )
        assertThat(message, `is`(expectedMessage))
    }

    @Test
    fun `should merge text messages into single blocks`() {
        // Merge at the start and the end
        val message = messageOf(
            "This is a ",
            "simple ",
            param("replace"),
            " case.",
            " With extra content"
        )
        val expectedMessage = CompoundMessage(
            arrayOf(
                MessageBlock("This is a simple "), // Merged first 2 messages into one
                ParameterBlock("{replace}"),
                MessageBlock(" case. With extra content") // Merged last 2 messages into one
            )
        )
        assertThat(message, `is`(expectedMessage))
    }

    @Test
    fun `should merge text messages on compound messages by combining edges`() {
        // Merge at the edges (compound1.last + compound2.first)
        val compound1 = compound(param("A"), msg("B"))
        val compound2 = compound(msg("C"), param("D"))
        val expectedMessage = CompoundMessage(
            arrayOf(
                ParameterBlock("{A}"),
                MessageBlock("BC"),
                ParameterBlock("{D}")
            )
        )
        assertThat(compound1 + compound2, `is`(expectedMessage))
    }

    @Test
    fun `should replace parameters`() {
        val message = messageOf(
            "This is a ",
            param("replace")
        ).resolveString(mapOf("replace" to "test"))
        assertThat(message, `is`("This is a test"))
    }

    @Test
    fun `should match by equals between LanguageMessage and String`() {
        assertThat(message("Hello"), `is`("Hello"))
    }

    @Test
    fun `should build and resolve replaced message`() {
        val message = buildMessage {
            append("This is")
            appendSpace()
            append("a ")
            appendParam("param")
        }.replace("param", "test")

        assertThat(message, `is`("This is a test"))
    }

    @Test
    fun `should resolve a stripped message`() {
        val message = buildMessage {
            append("&4This is a stripped message")
        }
        assertThat(message.stripped(), `is`("This is a stripped message"))
    }

    @Test
    fun `should correctly mutate messages`() {
        val message = buildMessage {
            appendReplacement("color", "&f")
            append("This is a colored message, this is a ")
            appendReplacement("accent", "&c")
            appendParam("param")
        }.replace("param", "parameter")

        val mutated = message.mutate {
            replacement {
                val color = if (it.key == "color") "&c" else "&4"
                repl(it.key, color)
            }
            parameter {
                repl("bold", "&l") + it
            }
        }
        assertThat(mutated.resolved(), `is`("§cThis is a colored message, this is a §4§lparameter"))
    }
}
