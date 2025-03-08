package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.language.buildMessage
import com.vexillum.plugincore.language.message
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class LanguageMessageTests {

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
        }
        message.replaceKey("param", "test")
        assertThat(message, `is`("This is a test"))
    }

    @Test
    fun `should resolve a stripped message`() {
        val message = buildMessage {
            append("&4This is a stripped message")
        }
        assertThat(message.stripped(), `is`("This is a stripped message"))
    }
}
