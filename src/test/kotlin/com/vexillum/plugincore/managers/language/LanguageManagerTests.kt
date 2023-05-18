package com.vexillum.plugincore.managers.language

import com.vexillum.plugincore.extensions.loadResource
import com.vexillum.plugincore.managerFactory
import com.vexillum.plugincore.managers.language.LocalLanguage.ARGENTINEAN_SPANISH
import com.vexillum.plugincore.managers.language.LocalLanguage.AUSTRALIAN_ENGLISH
import com.vexillum.plugincore.managers.language.LocalLanguage.CHINESE
import com.vexillum.plugincore.managers.language.LocalLanguage.ENGLISH
import com.vexillum.plugincore.managers.language.LocalLanguage.SPANISH
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
        val testDataFolder = this::class.loadResource("data")?.toFile()
            ?: error("Must load the file in that location")

        val managerFactory = managerFactory(testDataFolder)
        languageManager = managerFactory.newLanguageManager(
            TestLanguage::class,
            "language",
            "language"
        )
        assertDoesNotThrow("Should load the languages") {
            languageManager.reload()
        }
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
    fun `should get a sibling language if the asked one is not found`() {
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
