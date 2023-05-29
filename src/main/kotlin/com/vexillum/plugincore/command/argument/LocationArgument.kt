package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.argument.VectorArgument.VectorDescriptor
import com.vexillum.plugincore.command.extractor.DoubleExtractor
import com.vexillum.plugincore.command.extractor.WorldExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.launcher.PluginCoreLauncher
import com.vexillum.plugincore.managers.language.LanguageAgent
import org.bukkit.Location
import org.bukkit.World

open class LocationArgument<Sender : LanguageAgent>(
    descriptor: (LanguageAgent) -> LocationDescriptor = { LocationDescriptor.of(it) },
    override val processor: ArgumentProcessor<Sender, Location, Location>? = null
) : Argument4<Sender, World, Double, Double, Double, Location>() {

    override val extractor1 = WorldExtractor<Sender> { descriptor(it).world }

    override val extractor2 = DoubleExtractor<Sender> { descriptor(it).vector.x }

    override val extractor3 = DoubleExtractor<Sender> { descriptor(it).vector.y }

    override val extractor4 = DoubleExtractor<Sender> { descriptor(it).vector.z }

    override val merger = { _: Sender, world: World, x: Double, y: Double, z: Double ->
        Location(world, x, y, z)
    }

    data class LocationDescriptor(
        val world: String,
        val vector: VectorDescriptor
    ) {

        companion object {

            fun of(agent: LanguageAgent) =
                PluginCoreLauncher.instance.withAgent(agent) {
                    LocationDescriptor(
                        world = resolve { command.descriptor.world },
                        vector = VectorDescriptor.of(agent)
                    )
                }
        }
    }
}
