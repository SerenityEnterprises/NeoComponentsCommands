package host.serenity.neo.components.commands.parser.provider

import host.serenity.neo.components.commands.parser.ArgumentParser

interface ArgumentParserSupplier {
    fun supply(type: Class<*>): ArgumentParser<*>?
}