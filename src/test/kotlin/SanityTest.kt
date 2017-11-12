
import host.serenity.neo.components.commands.Command
import host.serenity.neo.components.commands.CommandManager
import org.junit.Assert
import org.junit.Test

class SanityTest {
    var hasRanTestCommand = false

    @Test
    fun test() {
        val commandManager = CommandManager()
        commandManager.registerCommand(Command.declare("test") {
            branch {
                hasRanTestCommand = true
            }
        })

        commandManager.executeCommand("test")

        Assert.assertEquals(true, hasRanTestCommand)
    }
}
