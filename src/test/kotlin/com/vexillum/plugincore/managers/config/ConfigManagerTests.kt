package com.vexillum.plugincore.managers.config

import com.vexillum.plugincore.pluginCore
import org.bukkit.Material
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.jupiter.api.Test

class ConfigManagerTests {

    data class TestConfig(
        val prop1: Int,
        val object1: Object1,
        val list1: List<Int>,
        val material1: Material
    )

    data class Object1(
        val a: String,
        val b: Int,
        val c: C
    )

    data class C(
        val text: String
    )

    @Test
    fun `should correctly load a config file`() {

        val managerFactory = pluginCore.managerFactory

        val configManager = managerFactory.newConfigManager(
            TestConfig::class,
            "testConfig",
            "data",
            "origin"
        )

        val loadedConfig = configManager()

        with(loadedConfig) {
            assertThat(prop1, `is`(5))
            with(object1) {
                assertThat(a, `is`("Hello"))
                assertThat(b, `is`(23))
                with(c) {
                    assertThat(text, `is`("There"))
                }
            }
            assertThat(list1, `is`(listOf(1, 2, 3)))
            assertThat(material1, `is`(Material.STONE))
        }
    }
}
