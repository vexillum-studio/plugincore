package com.vexillum.plugincore.command

import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.command.suggestion.CommandSuggestion
import com.vexillum.plugincore.command.suggestion.SubCommandSuggestion
import com.vexillum.plugincore.command.suggestion.UsageSuggestion
import com.vexillum.plugincore.extensions.takeWhen
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

    override fun execute(session: CommandSession<Sender>) {
        // Check for permission
        checkPermission(session)
        val args = session.args
        var lastException: Exception? = null
        // Check for subcommand matching
        args.firstOrNull()?.let { firstArg ->
            subCommands.forEach { subCommand ->
                if (subCommand.matches(firstArg)) {
                    try {
                        subCommand.execute(session.moveToNextArg())
                        return
                    } catch (e: CommandException) {
                        lastException = e
                    }
                }
            }
        }
        var maxMatchingScore = -1.0
        usages.forEach { usage ->
            val context = usage.execute(session.resetSession())
            if (context.completed) {
                return
            }
            val executionException = context.exception
            if (executionException != null && context.matchingScore > maxMatchingScore) {
                lastException = executionException
                maxMatchingScore = context.matchingScore
            }
        }
        lastException?.let {
            throw it
        }
    }

    override fun matches(label: String): Boolean =
        name.equals(label, ignoreCase = true) || aliases.any {
            it.equals(label, ignoreCase = true)
        }

    override fun autocomplete(session: CommandSession<Sender>): MutableList<String> {
        checkPermission(session)
        val args = session.args
        // Look for sub command tab complete
        if (args.isNotEmpty()) {
            for (subcommand in subCommands) {
                if (subcommand.matches(args.first())) {
                    return subcommand.autocomplete(session.moveToNextArg())
                }
            }
        }

        val lastArg = session.currentArg
        val autocompletes = mutableListOf<CommandSuggestion<Sender>>()

        autocompleteSubCommands(autocompletes)
        autocompleteUsages(session, autocompletes)

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
            .map { it.describe(session) }
            .toMutableList()
    }

    private fun checkPermission(session: CommandSession<*>) {
        if (permission?.let { session.agent.hasPermission(it) } == false) {
            session.languageException { command.permissionMessage }
        }
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
        session: CommandSession<Sender>,
        autocompletes: MutableList<CommandSuggestion<Sender>>
    ) {
        val args = session.args
        for (usage in usages) {
            val arguments = usage.arguments
            val totalSlots = arguments.sumOf { it.slots }
            // Skip usages that we can't complete anymore
            if (args.size > totalSlots) continue
            val context = usage.validate(session.resetSession())
            if (!context.executedSuccessfully) continue
            val lastExtractor = context.lastExtractor ?: continue
            val currentArg = context.currentArg
            val extractorAutocompletes = mutableListOf<CommandSuggestion<Sender>>()
            if (!currentArg.isNullOrEmpty()) {
                lastExtractor.autocomplete(session.agent, currentArg)
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
                    autocompletes.add(UsageSuggestion(lastExtractor.descriptor(session)))
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
