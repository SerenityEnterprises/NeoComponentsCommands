package host.serenity.neo.components.commands.utils

import java.util.regex.Pattern

fun join(strings: Array<String>, delimiter: String): String {
    val str = StringBuilder()
    for (string in strings) {
        str.append(string)
        str.append(delimiter)
    }

    return str.substring(0, str.length - delimiter.length)
}

fun splitExceptingQuotes(string: String, stripQuotes: Boolean): Array<String> {
    val list = arrayListOf<String>()
    val m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(string)
    while (m.find())
        list.add(if (stripQuotes) m.group(1).replace("\"", "") else m.group(1))

    if (string.endsWith(" "))
        list.add("")

    return list.toTypedArray()
}