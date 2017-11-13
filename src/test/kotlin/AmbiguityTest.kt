import host.serenity.neo.components.commands.Command
import host.serenity.neo.components.commands.CommandManager
import org.junit.Test

class AmbiguityTest {
    @Test
    fun test() {
        val commandManager = CommandManager()

        commandManager.registerCommand(Command.declare("test") {
            branch("mode") {
                println("Empty!")
            }

            branch("mode") { s: String ->
                println("Mode should be set to '$s'.")
            }
        })

        commandManager.executeCommand("test mode")
        commandManager.executeCommand("test mode 1")
        commandManager.executeCommand("test mode 2")
    }
}