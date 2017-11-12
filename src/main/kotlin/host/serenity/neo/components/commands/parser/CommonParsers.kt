package host.serenity.neo.components.commands.parser

import host.serenity.neo.components.commands.context.CommandExecutionContext
import host.serenity.neo.components.commands.parser.provider.ArgumentParserSupplier
import java.io.File
import java.io.FileNotFoundException
import java.util.*




class UUIDParser : ArgumentParser<UUID> {
    private val UUID_ZERO = UUID(0, 0)

    override fun parse(context: CommandExecutionContext): UUID {
        return UUID.fromString(context.arguments.next())
    }

    override fun provideSuggestions(context: CommandExecutionContext): List<String> {
        return listOf(UUID_ZERO, UUID.randomUUID(), UUID.randomUUID()).map { it.toString() }
    }
}

class GenericEnumParser<out E : Enum<*>>(type: Class<E>) : ArgumentParser<E> {
    private val expected = type.simpleName
    private val suggestions = mutableListOf<String>()
    private val options = mutableMapOf<String, E>()

    init {
        for (element in type.enumConstants) {
            val elementName = applyCapitalization(element)
            this.options.put(elementName.toLowerCase(), element)
            suggestions += elementName
        }
    }

    fun <E : Enum<*>> applyCapitalization(element: E): String {
        fun capitalize(str: String, vararg delimiters: Char): String {
            if (str.isEmpty() || delimiters.isEmpty())
                return str

            val buffer = str.toCharArray()
            var capitalizeNext = true
            for (i in buffer.indices) {
                val ch = buffer[i]

                when {
                    ch in delimiters -> { capitalizeNext = true; buffer[i] = ' ' }
                    capitalizeNext -> {
                        buffer[i] = Character.toTitleCase(ch)
                        capitalizeNext = false
                    }
                    else -> buffer[i] = Character.toLowerCase(ch)
                }
            }

            return String(buffer)
        }

        var elementName = element.toString()
        if (elementName.startsWith("_")) {
            elementName = elementName.substring(1)
        } else if (elementName == elementName.toUpperCase()) {
            elementName = capitalize(elementName, '_').replace("_", "")
        }
        return elementName
    }

    override fun provideSuggestions(context: CommandExecutionContext): List<String> {
        return suggestions
    }

    override fun parse(context: CommandExecutionContext): E {
        return options[context.arguments.next().toLowerCase()]!!
    }
}

class GenericEnumParserSupplier : ArgumentParserSupplier {
    override fun supply(type: Class<*>): ArgumentParser<*>? {
        if (type.isEnum) {
            @Suppress("UNCHECKED_CAST")
            return GenericEnumParser(type as Class<Enum<*>>)
        }

        return null
    }
}

class FileParser(val directory: File, val extension: String?) : ArgumentParser<File> {
    private val EXTRANEOUS = "[ \\t_\\-'\"]"

    override fun parse(context: CommandExecutionContext): File {
        val name = stripExtraneous(context.arguments.next())

        var best: File? = null
        for (file in directory.listFiles()) {
            if (file.isDirectory)
                continue

            val fileName = stripExtraneous(file.nameWithoutExtension)
            val extension = file.extension
            if (extension == null || extension.equals(extension, ignoreCase = true)) {
                if (fileName == name) {
                    best = file
                    break
                }
                if (fileName.contains(name)) {
                    best = file
                }
            }
        }

        return best ?: throw FileNotFoundException(name)
    }

    private fun stripExtraneous(input: String): String {
        return input.replace(EXTRANEOUS.toRegex(), "").toLowerCase()
    }

    override fun provideSuggestions(context: CommandExecutionContext): List<String> {
        val fileNames = mutableListOf<String>()

        directory.listFiles()
                .filter { file -> !file.isDirectory }
                .forEach { file -> fileNames.add(file.nameWithoutExtension.replace(" ", "_")) }

        return fileNames
    }
}
