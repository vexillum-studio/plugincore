package com.vexillum.plugincore.command.processor.mapper

import com.vexillum.plugincore.managers.language.LanguageAgent

abstract class DoubleMapper<Sender : LanguageAgent, BaseType : Any> : ArgumentMapper<Sender, BaseType, Double> {
    override val clazz = Double::class.java
}
