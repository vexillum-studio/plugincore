package com.vexillum.plugincore.command.processor.mapper

import com.vexillum.plugincore.language.LanguageAgent

abstract class IntMapper<Sender : LanguageAgent, BaseType : Any> : ArgumentMapper<Sender, BaseType, Int> {
    override val clazz = Int::class.java
}
