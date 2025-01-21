package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.extractor.DoubleExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.util.Vector

open class VectorArgument<Sender : LanguageAgent>(
    descriptor: (CommandUser<*>) -> VectorDescriptor = { VectorDescriptor.of(it) },
    override val processor: ArgumentProcessor<Sender, Vector, Vector>? = null
) : Argument3<Sender, Double, Double, Double, Vector>() {

    override val extractor1 = DoubleExtractor<Sender> { descriptor(it).x }

    override val extractor2 = DoubleExtractor<Sender> { descriptor(it).y }

    override val extractor3 = DoubleExtractor<Sender> { descriptor(it).z }

    override val merger = { _: CommandUser<Sender>, x: Double, y: Double, z: Double ->
        Vector(x, y, z)
    }

    data class VectorDescriptor(
        val x: String,
        val y: String,
        val z: String
    ) {

        companion object {

            fun <Sender : LanguageAgent> of(user: CommandUser<Sender>) =
                with(user) {
                    VectorDescriptor(
                        x = resolve { command.descriptor.x },
                        y = resolve { command.descriptor.y },
                        z = resolve { command.descriptor.z }
                    )
                }
        }
    }
}
