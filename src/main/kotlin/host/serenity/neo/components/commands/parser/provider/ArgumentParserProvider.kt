package host.serenity.neo.components.commands.parser.provider

import host.serenity.neo.components.commands.parser.ArgumentParser

class ArgumentParserProvider {
    private val parserRegistry = mutableMapOf<Class<*>, ArgumentParser<*>>()
    private val suppliers = mutableListOf<ArgumentParserSupplier>()

    @Suppress("UNCHECKED_CAST")
    fun getParser(type: Class<*>): ArgumentParser<*>? {
        suppliers
                .mapNotNull { it.supply(type) }
                .forEach { return it }

        return parserRegistry[type]
    }

    fun <T> register(type: Class<T>, parser: ArgumentParser<T>) {
        parserRegistry.put(type, parser)
    }

    fun addSupplier(supplier: ArgumentParserSupplier) {
        suppliers += supplier
    }
}
