package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.language.LocalLanguage.*
import com.vexillum.plugincore.language.Message
import com.vexillum.plugincore.language.resolve
import com.vexillum.plugincore.pluginCoreBase
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow

class LanguageManagerTests {

    data class TestLanguage(
        val message1: Message
    )

    private lateinit var languageManager: LanguageManager<TestLanguage>

    @BeforeEach
    fun setUp() {
        languageManager = pluginCoreBase.managerFactory.newLanguageManager(
            TestLanguage::class,
            "data/language",
            "language-test"
        )
        assertDoesNotThrow("Should load the languages") {
            languageManager.reload()
        }
    }

    @Test
    fun `should correctly load a language and resolve a message from file`() {
        val language = languageManager.language(ENGLISH)
        assertThat(language.resolve { message1 }, `is`("Hello §4123"))
    }

    @Test
    fun `should correctly get EN language defined in the languages folder`() {
        assertThat(languageManager.language(ENGLISH), `is`(notNullValue()))
    }

    @Test
    fun `should get EN language if a children is requested and not loaded`() {
        assertThat(languageManager.language(AUSTRALIAN_ENGLISH).localeLanguage, `is`(ENGLISH))
    }

    @Test
    fun `should get a sibling language if the requested language is not found`() {
        assertThat(languageManager.language(SPANISH).localeLanguage, `is`(ARGENTINEAN_SPANISH))
    }

    @Test
    fun `should get a child language if a parent is asked and not found`() {
        assertThat(languageManager.language(SPANISH).localeLanguage, `is`(ARGENTINEAN_SPANISH))
    }

    @Test
    fun `should get EN language by default if the requested is not loaded and no related one is found`() {
        assertThat(languageManager.language(CHINESE).localeLanguage, `is`(ENGLISH))
    }
}
