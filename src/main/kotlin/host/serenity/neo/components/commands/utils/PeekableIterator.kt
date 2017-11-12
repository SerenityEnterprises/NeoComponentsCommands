package host.serenity.neo.components.commands.utils

class PeekableIterator<T>(internal val wrapped: ListIterator<T>) : Iterator<T> {
    override fun hasNext(): Boolean {
        return wrapped.hasNext()
    }

    override fun next(): T {
        return wrapped.next()
    }

    fun peek(): T {
        val next = wrapped.next()
        wrapped.previous()

        return next
    }

    internal fun previous(): T = wrapped.previous()
}