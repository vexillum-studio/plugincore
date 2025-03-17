package com.vexillum.plugincore.stdout

import com.vexillum.plugincore.extensions.loadResourceAsStream
import com.vexillum.plugincore.language.Language
import com.vexillum.plugincore.language.LocaleResolver
import com.vexillum.plugincore.language.deserializer.DeserializerResult
import com.vexillum.plugincore.language.deserializer.MessageDeserializer
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageReplacements

data class AnsiColors(
    val black: Message,
    val red: Message,
    val green: Message,
    val yellow: Message,
    val blue: Message,
    val magenta: Message,
    val cyan: Message,
    val white: Message,
    val bright: BrightColors,
    val background: BackgroundColors,
    val reset: Message,
    val bold: Message,
    val dim: Message,
    val italic: Message,
    val underline: Message,
    val reverse: Message,
    val hidden: Message,
    val strikethrough: Message
) : Language

data class BrightColors(
    val black: Message,
    val red: Message,
    val green: Message,
    val yellow: Message,
    val blue: Message,
    val magenta: Message,
    val cyan: Message,
    val white: Message
)

data class BackgroundColors(
    val black: Message,
    val red: Message,
    val green: Message,
    val yellow: Message,
    val blue: Message,
    val magenta: Message,
    val cyan: Message,
    val white: Message
)

fun <R> ansiContext(block: LocaleResolver<AnsiColors>.() -> R): R =
    Ansi.run(block)

internal object Ansi : LocaleResolver<AnsiColors> {

    private const val ANSI_COLORS_FILE = "stdout/ansiColors.json"
    private val colors: DeserializerResult<AnsiColors>

    init {
        val resourceStream = this::class.loadResourceAsStream(ANSI_COLORS_FILE)
            ?: error("Could not load $ANSI_COLORS_FILE file")
        colors = MessageDeserializer.deserialize(
            resourceStream,
            AnsiColors::class
        )
    }

    override val value: AnsiColors by lazy {
        colors.language
    }

    override val replacements: MessageReplacements by lazy {
        colors.replacements
    }
}
