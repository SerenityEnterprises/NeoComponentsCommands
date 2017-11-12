package host.serenity.neo.components.commands.context

import host.serenity.neo.components.commands.utils.PeekableIterator

class CommandExecutionContext(val arguments: PeekableIterator<String>, val parameters: Array<Class<*>>, var currentParameter: Int)
