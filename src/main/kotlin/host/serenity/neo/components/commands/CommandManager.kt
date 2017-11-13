package host.serenity.neo.components.commands

import host.serenity.neo.components.commands.context.CommandExecutionContext
import host.serenity.neo.components.commands.exceptions.*
import host.serenity.neo.components.commands.parser.ArgumentParser
import host.serenity.neo.components.commands.utils.PeekableIterator
import host.serenity.neo.components.commands.utils.splitExceptingQuotes

class CommandManager {
    val commands = mutableListOf<Command>()
    private val parserRegistry = mutableMapOf<Class<*>, ArgumentParser<*>>()

    constructor() {
        addDefaultParsers(parserRegistry)
    }

    constructor(registerDefaultParsers: Boolean) {
        if (registerDefaultParsers) {
            addDefaultParsers(parserRegistry)
        }
    }

    fun <T> registerParser(type: Class<T>, parser: ArgumentParser<T>) {
        parserRegistry.put(type, parser)
    }

    private fun verifyCommand(command: Command) {
        // TODO: Prevent name collisions for commands / aliases

        command.branches.forEach {
            it.parameterTypes
                    .filter { it !in parserRegistry }
                    .forEach { throw MissingParserException(it) }
        }
    }

    @Throws(CommandRegistrationException::class)
    fun registerCommand(declaration: CommandDeclaration) {
        try {
            val command = Command(declaration.name, declaration.aliases.toTypedArray())
            command.branches.addAll(declaration.branches.asSequence()
                    .map { Command.CommandBranch(it.branchName, it.aliases.toTypedArray(), it.parameterTypes, it.handler, it.typeHints) })

            verifyCommand(command)
            commands += command
        } catch (e: Exception) {
            throw CommandRegistrationException(e)
        }
    }

    @Throws(CommandExecutionException::class)
    fun executeCommand(fullCommand: String) {
        try {
            val args = splitExceptingQuotes(fullCommand, true).toList()
            if (args.isEmpty())
                throw CommandNotFoundException("")

            val commandName = args[0]

            val matchingCommands = commands.filter { commandMatches(commandName, it) }
            when {
                matchingCommands.isEmpty() -> throw CommandNotFoundException(commandName)
                matchingCommands.size > 1 -> throw CommandIsAmbiguousException(commandName)
            }

            val command = matchingCommands.first()

            fun isBranchViable(branch: Command.CommandBranch, args: List<String>): Boolean {
                try {
                    val argObjects = parseArgumentsForBranch(branch, args)
                    if (argObjects.size == branch.parameterTypes.size) {
                        return true
                    }
                } catch (e: Exception) {
                    return false
                }

                return false
            }

            val viableBranches = command.branches.filter { branchMatches(args, it) }.filter { isBranchViable(it, args) }

            val viableNamedBranches = viableBranches.filter { it.name != null }
            if (viableNamedBranches.size > 1)
                throw BranchesAreAmbiguousException(commandName, args[1])

            // Loop through named branches first, as branch names have priority over arguments.
            for (branch in viableNamedBranches) {
                executeBranch(branch, args)
                return
            }

            for (branch in viableBranches.filter { it.name == null }) {
                executeBranch(branch, args)
                return
            }

            throw NoMatchingBranchesException()
        } catch (e: Exception) {
            throw CommandExecutionException(e)
        }
    }

    fun completeCommand(fullCommand: String): Array<String> {
        return completeCommandDuplicatesSpaces(fullCommand)
                .map { if (it.toCharArray().any { it.isWhitespace() }) "\"$it\"" else it } // Wrap suggestions containing spaces in quotes
                .toSet() // Remove duplicates
                .toTypedArray()
    }

    private fun completeCommandDuplicatesSpaces(fullCommand: String): List<String> {
        val args = splitExceptingQuotes(fullCommand, true).toList()

        if (args.isEmpty())
            return commands.map { it.name }

        if (args.size == 1) {
            val namesAndAliasesOfCommands = mutableListOf<String>().apply {
                addAll(commands.map { it.name })
                commands.forEach { addAll(it.aliases) }
            }

            return namesAndAliasesOfCommands.filter { it.toLowerCase().startsWith(fullCommand.toLowerCase()) }
        }

        val commandName = args[0]
        val matchingCommands = commands.filter { commandMatches(commandName, it) }
        when {
            matchingCommands.isEmpty() -> return emptyList()
            matchingCommands.size > 1 -> throw CommandIsAmbiguousException(commandName)
        }

        val command = matchingCommands.first()

        if (args[1].isBlank())
            return command.branches.mapNotNull { it.name }

        val matchingBranches = mutableListOf<String>().apply {
            addAll(command.branches.mapNotNull { it.name })
            command.branches.forEach { addAll(it.aliases) }
        }.filter { it.startsWith(args[1]) }

        if (args.size == 2)
            return matchingBranches

        // TODO: Tab completion for parameters
        // - Solve ambiguity issues
        // ????

        return emptyList()
    }

    private fun parseArgumentsForBranch(branch: Command.CommandBranch, args: List<String>): Array<Any> {
        val parsers = mutableListOf<ArgumentParser<*>>()
        for ((index, parameterType) in branch.parameterTypes.withIndex()) {
            if (index in branch.typeHints) {
                parsers.add(branch.typeHints[index]!!)
            } else {
                if (parameterType !in parserRegistry)
                    throw MissingParserException(parameterType)

                parsers.add(parserRegistry[parameterType]!!)
            }
        }

        val startIndex = if (branch.name == null) 1 else 2

        if (parsers.isEmpty() && args.size > startIndex) {
            throw ParsingException(InvalidArgumentCount(branch.name ?: "<blank>"))
        }

        val argsIterator = PeekableIterator(args.listIterator(startIndex))

        val context = CommandExecutionContext(argsIterator, branch.parameterTypes, 0)

        val argumentObjects = mutableListOf<Any>()

        val minArgs = parsers.map { it.minimumAcceptedArguments() }.sum()

        if (args.size - startIndex >= minArgs && args.size - startIndex >= parsers.size) {
            for ((index, parser) in parsers.withIndex()) {
                context.currentParameter = index

                try {
                    argumentObjects += parser.parse(context)!!
                } catch (e: Exception) {
                    throw ParsingException(e)
                }
            }
        } else {
            throw ParsingException(InvalidArgumentCount(branch.name ?: "<blank>"))
        }

        return argumentObjects.toTypedArray()
    }

    private fun executeBranch(branch: Command.CommandBranch, args: List<String>) {
        val argumentObjects = parseArgumentsForBranch(branch, args)
        branch.execute(*argumentObjects)
    }

    private fun commandMatches(commandName: String, command: Command): Boolean {
        return command.name.equals(commandName, ignoreCase = true) ||
                command.aliases.any { it.equals(commandName, ignoreCase = true) }
    }

    private fun branchMatches(args: List<String>, branch: Command.CommandBranch): Boolean {
        return branch.name == null ||
                (args.size > 1 && (branch.name.equals(args[1], ignoreCase = true) ||
                        branch.aliases.any { it.equals(args[1], ignoreCase = true) }))
    }
}
