package host.serenity.neo.components.commands

import host.serenity.neo.components.commands.parser.*
import host.serenity.neo.components.commands.parser.provider.ArgumentParserProvider
import java.util.*

fun addDefaultParsers(parserProvider: ArgumentParserProvider) {
    parserProvider.register(String::class.java, StringParser())

    parserProvider.register(Int::class.java, IntParser())
    parserProvider.register(Float::class.java, FloatParser())
    parserProvider.register(Double::class.java, DoubleParser())
    parserProvider.register(Short::class.java, ShortParser())
    parserProvider.register(Long::class.java, LongParser())
    parserProvider.register(Char::class.java, CharParser())

    parserProvider.register(UUID::class.java, UUIDParser())

    parserProvider.addSupplier(GenericEnumParserSupplier())
}
