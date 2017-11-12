import host.serenity.neo.components.commands.Command
import host.serenity.neo.components.commands.CommandManager
import org.junit.Assert
import org.junit.Test

enum class Num {
    ONE, TWO, THREE, TWENTY_FOUR
}

class EnumParamTest {
    var result: Num = Num.ONE

    @Test
    fun test() {
        val commandManager = CommandManager()

        commandManager.registerCommand(Command.declare("test") {
            branch { n: Num ->
                result = n
            }
        })

        commandManager.executeCommand("test two")
        Assert.assertEquals(Num.TWO, result)
        commandManager.executeCommand("test three")
        Assert.assertEquals(Num.THREE, result)

        Assert.assertArrayEquals(arrayOf("One", "Two", "Three", "\"Twenty Four\""), commandManager.completeCommand("test "))
    }
}