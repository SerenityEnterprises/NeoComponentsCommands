import host.serenity.neo.components.commands.Command
import host.serenity.neo.components.commands.CommandManager
import org.junit.Assert
import org.junit.Test

class EchoTest {
    var output: String = ""

    @Test
    fun test() {
        val commandManager = CommandManager()
        commandManager.registerCommand(Command.declare("echo") {
            alias("output")

            branch("reversed") { s: String ->
                output = StringBuilder(s).reverse().toString()
            }

            branch { s: String ->
                output = s
            }
        })

        commandManager.executeCommand("echo Hello, world!")
        Assert.assertEquals("Hello, world!", output)

        commandManager.executeCommand("output reversed Hello, world!")
        Assert.assertEquals("!dlrow ,olleH", output)
    }
}
