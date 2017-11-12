package host.serenity.neo.components.commands

import host.serenity.neo.components.commands.parser.ArgumentParser
import java.lang.reflect.ParameterizedType

class Command(val name: String, val aliases: Array<String>) {
    companion object {
        fun declare(name: String, init: CommandDeclaration.() -> Unit): CommandDeclaration {
            val declaration = CommandDeclaration(name)
            init(declaration)

            return declaration
        }
    }

    val branches = mutableListOf<CommandBranch>()
    class CommandBranch(val name: String?, val aliases: Array<String>, val parameterTypes: Array<Class<*>>, private val handler: Function<*>, val typeHints: Map<Int, ArgumentParser<*>>) {
        fun execute(vararg args: Any) {
            handler.javaClass.declaredMethods.first { it.name == "invoke" }.apply { this.isAccessible = true }.invoke(handler, *args)
        }
    }
}

class CommandDeclaration internal constructor(val name: String) {
    internal val branches = mutableListOf<BranchDeclaration>()
    internal val aliases = mutableListOf<String>()

    fun alias(aliasName: String) {
        aliases += aliasName
    }

    fun branch(branchName: String? = null, handler: Function<*>): BranchDeclaration {
        val params = handler.javaClass.genericInterfaces
        for (param in params) {
            if (param is ParameterizedType) {
                val parameterTypes = mutableListOf<Class<*>>()

                param.actualTypeArguments
                        .filterIndexed { index, _ -> index != param.actualTypeArguments.lastIndex }
                        .filterIsInstance<Class<*>>()
                        .forEach { parameterTypes += it }

                val declaration = BranchDeclaration(branchName, handler, parameterTypes.toTypedArray())

                branches += declaration
                return declaration
            }
        }

        throw IllegalStateException()
    }

    inner class BranchDeclaration internal constructor(val branchName: String?, val handler: Function<*>, val parameterTypes: Array<Class<*>>) {
        internal val aliases = mutableListOf<String>()
        internal val typeHints = mutableMapOf<Int, ArgumentParser<*>>()

        fun alias(aliasName: String): BranchDeclaration {
            aliases += aliasName

            return this
        }

        fun typeHint(index: Int, parser: ArgumentParser<*>): BranchDeclaration {
            typeHints.put(index, parser)

            return this
        }
    }
}
