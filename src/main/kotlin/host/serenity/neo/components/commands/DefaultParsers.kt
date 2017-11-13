package host.serenity.neo.components.commands

import host.serenity.neo.components.commands.parser.*

fun addDefaultParsers(parserRegistry: MutableMap<Class<*>, ArgumentParser<*>>) {
    parserRegistry.put(String::class.java, StringParser())

    parserRegistry.put(Int::class.java, IntParser())
    parserRegistry.put(Float::class.java, FloatParser())
    parserRegistry.put(Double::class.java, DoubleParser())
    parserRegistry.put(Short::class.java, ShortParser())
    parserRegistry.put(Long::class.java, LongParser())
    parserRegistry.put(Char::class.java, CharParser())
}
