package com.vexillum.plugincore.manager.config

import com.vexillum.plugincore.extensions.loadResourceAsFile
import com.vexillum.plugincore.extensions.loadResourceAsStream
import com.vexillum.plugincore.extensions.loadResourceAsString
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.notNullValue
import org.junit.jupiter.api.Test

class KClassExtensionsTests {

    @Test
    fun `should load a resource from a class as stream`() {
        val loadedResource = this::class.loadResourceAsStream("data/banner.txt")
        assertThat(loadedResource, `is`(notNullValue()))
    }

    @Test
    fun `should load a resource from a class as string`() {
        val loadedResource = this::class.loadResourceAsString("data/banner.txt")
        assertThat(loadedResource, `is`(notNullValue()))
    }
    @Test
    fun `should load a resource from a class as file`() {
        val loadedResource = this::class.loadResourceAsFile("data/banner.txt")
        assertThat(loadedResource, `is`(notNullValue()))
    }

}