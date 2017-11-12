
import host.serenity.neo.components.commands.Command
import host.serenity.neo.components.commands.CommandManager
import host.serenity.neo.components.commands.context.CommandExecutionContext
import host.serenity.neo.components.commands.parser.ArgumentParser
import org.junit.Assert
import org.junit.Test

class StringParserWithSuggestions : ArgumentParser<String> {
    override fun parse(context: CommandExecutionContext): String {
        return context.arguments.next()
    }

    override fun provideSuggestions(context: CommandExecutionContext): List<String> {
        return listOf("aa", "bb", "cc")
    }
}

class CompletionTest {
    @Test
    fun test() {
        val commandManager = CommandManager()

        commandManager.registerCommand(Command.declare("complete") {
            branch("branch1") {

            }

            branch("branch2") { s: String ->

            }.typeHint(0, StringParserWithSuggestions())
        })

        Assert.assertArrayEquals(arrayOf("branch1", "branch2"), commandManager.completeCommand("complete "))
        Assert.assertArrayEquals(arrayOf("branch1", "branch2"), commandManager.completeCommand("complete br"))

        Assert.assertArrayEquals(arrayOf("aa", "bb", "cc"), commandManager.completeCommand("complete branch2 "))
        Assert.assertArrayEquals(arrayOf("aa"), commandManager.completeCommand("complete branch2 a"))
        Assert.assertArrayEquals(arrayOf("bb"), commandManager.completeCommand("complete branch2 bb"))
        Assert.assertArrayEquals(emptyArray(), commandManager.completeCommand("complete branch2 bb "))
    }
}