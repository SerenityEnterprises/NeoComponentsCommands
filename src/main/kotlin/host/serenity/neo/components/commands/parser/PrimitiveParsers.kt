package host.serenity.neo.components.commands.parser

import host.serenity.neo.components.commands.context.CommandExecutionContext

class StringParser : ArgumentParser<String> {
    override fun parse(context: CommandExecutionContext): String {
        return if (context.currentParameter == context.parameters.lastIndex) {
            buildString {
                for (arg in context.arguments) {
                    append(arg)

                    if (context.arguments.hasNext())
                        append(" ")
                }
            }
        } else {
            context.arguments.next()
        }
    }
}


class IntParser : ArgumentParser<Int> {
    override fun parse(context: CommandExecutionContext): Int {
        return context.arguments.next().toInt()
    }
}

class FloatParser : ArgumentParser<Float> {
    override fun parse(context: CommandExecutionContext): Float {
        return context.arguments.next().toFloat()
    }
}

class DoubleParser : ArgumentParser<Double> {
    override fun parse(context: CommandExecutionContext): Double {
        return context.arguments.next().toDouble()
    }
}

class ShortParser : ArgumentParser<Short> {
    override fun parse(context: CommandExecutionContext): Short {
        return context.arguments.next().toShort()
    }
}

class LongParser : ArgumentParser<Long> {
    override fun parse(context: CommandExecutionContext): Long {
        return context.arguments.next().toLong()
    }
}

class CharParser : ArgumentParser<Char> {
    override fun parse(context: CommandExecutionContext): Char {
        val chars = context.arguments.next().toCharArray()
        assert(chars.size == 1)

        return chars.first()
    }
}