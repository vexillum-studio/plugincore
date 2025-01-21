package com.vexillum.plugincore.command.argument

import com.vexillum.plugincore.command.argument.VectorArgument.VectorDescriptor
import com.vexillum.plugincore.command.extractor.DoubleExtractor
import com.vexillum.plugincore.command.processor.ArgumentProcessor
import com.vexillum.plugincore.command.session.CommandUser
import com.vexillum.plugincore.entities.PluginPlayer
import org.bukkit.Location

open class RelativeLocationArgument<Sender : PluginPlayer>(
    descriptor: (CommandUser<*>) -> VectorDescriptor = { VectorDescriptor.of(it) },
    override val processor: ArgumentProcessor<Sender, Location, Location>? = null
) : Argument3<Sender, Double, Double, Double, Location>() {

    override val extractor1 = DoubleExtractor<Sender> { descriptor(it).x }

    override val extractor2 = DoubleExtractor<Sender> { descriptor(it).y }

    override val extractor3 = DoubleExtractor<Sender> { descriptor(it).z }

    override val merger = { user: CommandUser<Sender>, x: Double, y: Double, z: Double ->
        Location(user.agent.world, x, y, z)
    }
}
