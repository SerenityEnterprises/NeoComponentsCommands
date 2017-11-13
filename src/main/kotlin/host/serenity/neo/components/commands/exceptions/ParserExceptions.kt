package host.serenity.neo.components.commands.exceptions

class MissingParserException(classToParse: Class<*>) : RuntimeException("No parser found for type: ${classToParse.name}")
class ParsingException(cause: Exception) : RuntimeException("Exception while parsing", cause)

class InvalidArgumentCount(branchName: String) : RuntimeException("Invalid amount of arguments for branch: $branchName")