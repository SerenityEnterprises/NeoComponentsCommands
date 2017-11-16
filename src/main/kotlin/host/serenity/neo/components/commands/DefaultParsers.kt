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


    // Java interop
    parserRegistry.put(java.lang.Integer::class.java, parserRegistry[Int::class.java]!!)
    parserRegistry.put(java.lang.Float::class.java, parserRegistry[Float::class.java]!!)
    parserRegistry.put(java.lang.Double::class.java, parserRegistry[Double::class.java]!!)
    parserRegistry.put(java.lang.Short::class.java, parserRegistry[Short::class.java]!!)
    parserRegistry.put(java.lang.Long::class.java, parserRegistry[Long::class.java]!!)
    parserRegistry.put(java.lang.Character::class.java, parserRegistry[Char::class.java]!!)
    // parserRegistry.put(java.lang.String::class.java, parserRegistry[String::class.java]!!)
}
