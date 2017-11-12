package host.serenity.neo.components.commands.parser

import host.serenity.neo.components.commands.context.CommandExecutionContext

interface ArgumentParser<out T> {
    fun parse(context: CommandExecutionContext): T
    fun provideSuggestions(context: CommandExecutionContext): List<String> = emptyList()
}
