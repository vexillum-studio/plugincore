package com.vexillum.plugincore.command.processor.mapper

import com.vexillum.plugincore.language.LanguageAgent

abstract class BooleanMapper<Sender : LanguageAgent, BaseType : Any> : ArgumentMapper<Sender, BaseType, Boolean> {

    override val clazz = Boolean::class.java
}
