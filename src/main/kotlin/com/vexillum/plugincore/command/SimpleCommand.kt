package com.vexillum.plugincore.command

import com.vexillum.plugincore.PluginCore
import com.vexillum.plugincore.command.session.CommandSession
import com.vexillum.plugincore.command.session.User
import com.vexillum.plugincore.command.suggestion.CommandSuggestion
import com.vexillum.plugincore.command.suggestion.SubCommandSuggestion
import com.vexillum.plugincore.command.suggestion.Suggestion
import com.vexillum.plugincore.command.suggestion.UsageSuggestion
import com.vexillum.plugincore.extensions.PluginCoreExtensions
import com.vexillum.plugincore.extensions.takeWhen
import com.vexillum.plugincore.language.LanguageAgent
import com.vexillum.plugincore.language.context.LanguageState
import com.vexillum.plugincore.language.message.Message
import com.vexillum.plugincore.language.message.MessageBuilder
import com.vexillum.plugincore.language.message.buildMessage
import com.vexillum.plugincore.language.message.message
import com.vexillum.plugincore.launcher.PluginCoreLauncher.Companion.pluginCoreInstance
import com.vexillum.plugincore.launcher.managers.language.PluginCoreLanguage
import com.vexillum.plugincore.util.sortByLevenshtein

@Suppress("LongParameterList")
internal class SimpleCommand<Sender : LanguageAgent>(
    override val pluginCore: PluginCore,
    override val startToken: String,
    override val name: CommandName,
    override val aliases: Set<CommandName>,
    override val description: ((LanguageAgent) -> String)?,
    override val permission: String?,
    override val usages: List<CommandUsage<Sender>>,
    override val subCommands: Set<SimpleCommand<Sender>>
) : Command<Sender>, PluginCoreExtensions {

    private var parent: SimpleCommand<Sender>? = null

    init {
        for (command in subCommands) {
            command.parent = this
        }
    }

    @Suppress("ComplexMethod")
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
                    } catch (e: Exception) {
                        lastException = e
                    }
                }
            }
        }
        var executionContext: ExecutionContext<Sender>? = null
        usages.forEach { usage ->
            usage.toString()
            val context = usage.execute(session.resetSession())
            if (context.completed) {
                return
            }
            val executionException = context.exception
            if (
                executionException != null &&
                (executionContext == null || context.matchingScore > executionContext!!.matchingScore)
            ) {
                lastException = executionException
                executionContext = context
            }
        }
        val exception = lastException ?: return
        val context = executionContext ?: return
        val lastArg = session.currentArg
        // Show all usages message when none of the usages are satisfied
        if (
            (lastArg.isNullOrEmpty() && parent == null) ||
            exception is ArgumentsNotDepletedException
        ) {
            session.agent.commandException { usagesMessage(this) }
        }
        if (exception is CommandException) {
            // Show command exceptions
            if (exception is ArgumentExtractException) {
                session.agent.commandException { argumentExtractMessage(session, exception, context) }
            }
            session.agent.commandException { exception.languageMessage }
        } else {
            // Show other type of exceptions
            exception.message?.let {
                session.agent.commandException { message(it) }
            }
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
        if (args.size == 1) {
            autocompleteSubCommands(autocompletes)
        }
        autocompleteUsages(session, autocompletes)

        return autocompletes
            .distinct()
            .let { rawList ->
                if (!lastArg.isNullOrEmpty()) {
                    rawList
                        .filter { autocomplete ->
                            !autocomplete.matchable || autocomplete.value.contains(lastArg, ignoreCase = true)
                        }
                        .sortByLevenshtein(lastArg) { value }
                        .take(MAX_SUGGESTIONS)
                } else {
                    rawList
                }
            }
            .take(MAX_SUGGESTIONS)
            .map { autocomplete ->
                autocomplete.describe(session).stripped()
            }
            .toMutableList()
    }

    private fun checkPermission(session: CommandSession<*>) {
        if (permission?.let { session.agent.hasPermission(it) } == false) {
            session.agent.commandException {
                resolve { command.permissionMessage }
            }
        }
    }

    private fun autocompleteSubCommands(
        autocompletes: MutableList<CommandSuggestion<Sender>>
    ) =
        takeWhen(subCommands.isNotEmpty()) {
            subCommands.map { it.name }.forEach { alias ->
                autocompletes.add(SubCommandSuggestion(message(alias)))
            }
        }

    @Suppress("ComplexMethod")
    private fun autocompleteUsages(
        session: CommandSession<Sender>,
        autocompletes: MutableList<CommandSuggestion<Sender>>
    ) {
        val args = session.args
        val extractorsWithError = mutableListOf<Message>()
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
            if (!currentArg.isNullOrEmpty() && !context.validLastExecution) {
                context.exception?.let { commandException ->
                    extractorsWithError.add(commandException.languageMessage)
                }
            }
            if (extractorAutocompletes.isEmpty()) {
                if (currentArg.isNullOrEmpty() || context.validLastExecution) {
                    autocompletes.add(UsageSuggestion(lastExtractor.descriptor(session)))
                }
            }
            autocompletes.addAll(extractorAutocompletes)
        }

        if (autocompletes.isEmpty() && extractorsWithError.isNotEmpty()) {
            autocompletes.add(Suggestion(extractorsWithError.first()))
        }
    }

    private fun usagesMessage(
        languageState: LanguageState<Sender, PluginCoreLanguage>
    ): Message =
        with(languageState) {
            val message = MessageBuilder(resolve { command.unknownUsage })
            for (usage in usages) {
                message.appendLine(usage.usageMessage(this))
            }
            message.build()
        }

    private fun CommandUsage<Sender>.usageMessage(
        languageState: LanguageState<Sender, PluginCoreLanguage>
    ): Message =
        with(languageState) {
            resolve { errorColor } + message("$startToken$name ") + describe(User(languageState))
        }

    private fun argumentExtractMessage(
        session: CommandSession<Sender>,
        exception: ArgumentExtractException,
        context: ExecutionContext<Sender>
    ): Message =
        with(session) {
            buildMessage {
                append(
                    resolve { command.incorrectUsage }.replace(
                        "argument",
                        exception.descriptor
                    )
                )
                mark()
                appendLine("$startToken$name")
                val argument = context.lastArgument!!
                val lastExtractor = context.lastExtractor
                var identAmount = 0
                for (extractor in argument.extractors) {
                    appendSpace()
                    if (extractor == lastExtractor) {
                        identAmount = measure() + resolve { command.descriptor.prefix }.strippedLength
                    }
                    append(extractor.describe(session))
                }
                appendLine()
                if (pluginCoreInstance.configManager().monospacedFont) {
                    // Append ident
                    appendSpace(identAmount)
                    // Append marker
                    append(resolve { errorAccent })
                    append(resolve { command.descriptor.marker })
                    append(resolve { errorColor })
                    appendSpace()
                }
                // Append actual error message
                append(exception.languageMessage)
            }
        }

    override fun <A : LanguageAgent> A.commandMessage(
        block: LanguageState<A, PluginCoreLanguage>.() -> Message
    ) =
        sendMessage(prefixedErrorMessage(block))

    override fun <A : LanguageAgent> A.commandException(
        block: LanguageState<A, PluginCoreLanguage>.() -> Message
    ): Nothing =
        throw CommandException(prefixedErrorMessage(block))

    private fun <A : LanguageAgent> A.prefixedErrorMessage(
        block: LanguageState<A, PluginCoreLanguage>.() -> Message
    ): Message =
        languageState(pluginCore).run {
            prefixedMessage {
                resolve { errorColor } + block()
            }
        }

    override fun toString() = name

    override fun hashCode(): Int =
        name.hashCode()

    override fun equals(other: Any?): Boolean =
        (other as? Command<*>)?.name == name

    companion object {
        private const val MAX_SUGGESTIONS = 20
    }
}
