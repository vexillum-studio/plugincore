package com.vexillum.plugincore.manager.config

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.extensions.loadResource
import com.vexillum.plugincore.extensions.loadResourceAsString
import com.vexillum.plugincore.manager.ManagerFactory
import org.bukkit.Material
import org.junit.jupiter.api.Test
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.mock
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`

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
        val testConfigFile = this::class.loadResource("/data/testConfig.json")!!
        val pluginCore = mock<PluginCore> {
            on { name } doReturn ("TestPlugin")
            on { logManager } doReturn (mock())
            on { dataFolder } doReturn (testConfigFile.parentFile)
        }

        val managerFactory = ManagerFactory(pluginCore)

        val configManager = managerFactory.newConfigManager(
            TestConfig::class,
            "testConfig",
            "data",
            "data"
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
