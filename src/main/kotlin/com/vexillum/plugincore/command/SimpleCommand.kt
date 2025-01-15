package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.command.suggestion.CommandSuggestion
import com.vexillum.plugincore.command.suggestion.SubCommandSuggestion
import com.vexillum.plugincore.command.suggestion.UsageSuggestion
import com.vexillum.plugincore.extensions.takeWhen
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.managers.language.LanguageAgent
import com.vexillum.plugincore.util.sortByLevenshtein

@Suppress("LongParameterList")
internal class SimpleCommand<Sender : LanguageAgent>(
    override val startToken: String,
    override val name: CommandName,
    override val aliases: Set<CommandName>,
    override val description: ((Sender) -> String)?,
    override val permission: String?,
    override val usages: List<CommandUsage<Sender>>,
    override val subCommands: Set<Command<Sender>>
) : Command<Sender> {

    override fun execute(sender: Sender, session: CommandSession) {
        if (permission?.let { sender.hasPermission(it) } == false) {
            pluginCoreInstance.withAgent(sender) {
                commandException { command.permissionMessage }
            }
        }
        val args = session.args

        var lastException: CommandException? = null
        // Check for subcommand matching
        args.firstOrNull()?.let { firstArg ->
            subCommands.forEach { subCommand ->
                if (subCommand.matches(firstArg)) {
                    try {
                        println("subcommand: " + subCommand.name)
                        subCommand.execute(sender, session.moveToNextArg())
                        return
                    } catch (e: CommandException) {
                        lastException = e
                    }
                }
            }
        }

        usages.forEach { usage ->
            try {
                println("usage: " + usage.describe(sender))
                usage.execute(sender, session)
                return
            } catch (e: CommandException) {
                lastException = e
            }
        }
        if (lastException != null) {
            println("threw: " + lastException!!::class.simpleName)
        }
    }

    override fun matches(label: String): Boolean =
        name.equals(label, ignoreCase = true) || aliases.any {
            it.equals(label, ignoreCase = true)
        }

    override fun autocomplete(sender: Sender, session: CommandSession): MutableList<String> {
        val args = session.args
        // Look for sub command tab complete
        if (args.isNotEmpty()) {
            for (subcommand in subCommands) {
                if (subcommand.matches(args.first())) {
                    return subcommand.autocomplete(sender, session.moveToNextArg())
                }
            }
        }

        val lastArg = session.currentArg
        val autocompletes = mutableListOf<CommandSuggestion<Sender>>()

        autocompleteSubCommands(autocompletes)
        autocompleteUsages(sender, session, autocompletes)

        return autocompletes
            .asSequence()
            .distinct()
            .let {
                if (!lastArg.isNullOrEmpty()) {
                    it.filter { autocomplete ->
                        !autocomplete.matchable || autocomplete.value.contains(lastArg, ignoreCase = true)
                    }.sortByLevenshtein(lastArg) { value }
                } else {
                    it
                }
            }
            .map { it.describe(sender) }
            .toMutableList()
    }

    private fun autocompleteSubCommands(
        autocompletes: MutableList<CommandSuggestion<Sender>>
    ) =
        takeWhen(subCommands.isNotEmpty()) {
            (subCommands.map { it.name } + subCommands.flatMap { it.aliases }).forEach { alias ->
                autocompletes.add(SubCommandSuggestion(alias))
            }
        }

    private fun autocompleteUsages(
        sender: Sender,
        session: CommandSession,
        autocompletes: MutableList<CommandSuggestion<Sender>>
    ) {
        val args = session.args
        for (usage in usages) {
            val arguments = usage.arguments
            val totalSlots = arguments.sumOf { it.slots }
            // Skip usages that we can't complete anymore
            if (args.size > totalSlots) continue
            val context = usage.validate(sender, session.resetSession())
            if (!context.executedSuccessfully) continue
            val lastExtractor = context.lastExtractor ?: continue
            val currentArg = context.currentArg
            val extractorAutocompletes = mutableListOf<CommandSuggestion<Sender>>()
            if (!currentArg.isNullOrEmpty()) {
                lastExtractor.autocomplete(sender, currentArg)
                    // Filter out autocompletes that match the current value
                    .filter { autocomplete ->
                        !autocomplete.value.equals(currentArg, ignoreCase = true)
                    }
                    .let {
                        extractorAutocompletes.addAll(it)
                    }
            }
            if (extractorAutocompletes.isEmpty()) {
                if (currentArg.isNullOrEmpty() || context.validLastExecution) {
                    autocompletes.add(UsageSuggestion(lastExtractor.descriptor(sender)))
                }
            }
            autocompletes.addAll(extractorAutocompletes)
        }
    }

    fun describe(sender: Sender) = buildString {
        append(name)
        description?.let {
            ": ${description.invoke(sender)}"
        }
    }

    override fun toString() = name

    override fun hashCode(): Int =
        name.hashCode()

    override fun equals(other: Any?): Boolean =
        (other as? Command<*>)?.name == name
}
