package host.serenity.neo.components.commands.exceptions

class CommandExecutionException(cause: Exception) : RuntimeException("Exception while executing command", cause)
class CommandRegistrationException(cause: Exception) : RuntimeException("Exception while registering command", cause)

class CommandNotFoundException(name: String) : RuntimeException("The command '$name' was not found.")
class CommandIsAmbiguousException(name: String) : RuntimeException("The command '$name' is ambiguous.")

class NoMatchingBranchesException : RuntimeException("No branches matching were found")
class BranchesAreAmbiguousException(command: String, branchName: String?) : RuntimeException("Branch '$branchName' is ambiguous for command '$command'.")