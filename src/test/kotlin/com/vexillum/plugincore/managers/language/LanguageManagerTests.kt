package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LocalLanguage.ARGENTINEAN_SPANISH
import com.vexillum.plugincore.language.LocalLanguage.AUSTRALIAN_ENGLISH
import com.vexillum.plugincore.language.LocalLanguage.CHINESE
import com.vexillum.plugincore.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.language.LocalLanguage.SPANISH
import com.vexillum.plugincore.language.message.Message
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
    ) : Language

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
        val languageTranslation = languageManager.translation(ENGLISH)
        assertThat(languageTranslation.resolve { message1 }, `is`(notNullValue()))
    }

    @Test
    fun `should correctly get EN language defined in the languages folder`() {
        assertThat(languageManager.translation(ENGLISH), `is`(notNullValue()))
    }

    @Test
    fun `should get EN language if a children is requested and not loaded`() {
        assertThat(languageManager.translation(AUSTRALIAN_ENGLISH).localeLanguage, `is`(ENGLISH))
    }

    @Test
    fun `should get a sibling language if the requested language is not found`() {
        assertThat(languageManager.translation(SPANISH).localeLanguage, `is`(ARGENTINEAN_SPANISH))
    }

    @Test
    fun `should get a child language if a parent is asked and not found`() {
        assertThat(languageManager.translation(SPANISH).localeLanguage, `is`(ARGENTINEAN_SPANISH))
    }

    @Test
    fun `should get EN language by default if the requested is not loaded and no related one is found`() {
        assertThat(languageManager.translation(CHINESE).localeLanguage, `is`(ENGLISH))
    }
}
