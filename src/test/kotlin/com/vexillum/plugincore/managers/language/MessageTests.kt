package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.language.CompoundMessage
import com.vexillum.plugincore.language.MessageBlock
import com.vexillum.plugincore.language.ParameterBlock
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class MessageTests : MessageHelper {

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
        val message = message("This is a ", param("replace"), " case")
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
        val message = message(
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
        val message = message(
            "This is a ",
            param("replace")
        ).resolveString(mapOf("replace" to "test"))
        assertThat(message, `is`("This is a test"))
    }
}
