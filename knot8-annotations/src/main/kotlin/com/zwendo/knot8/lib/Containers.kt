package com.zwendo.knot8.lib

interface BasicContainer<E : Any> {
    fun add(e: E)

    fun remove(e: Any)

    operator fun plusAssign(e: E): Unit = add(e)

    operator fun minusAssign(e: Any): Unit = remove(e)
}

interface HasBasicContainer<E : Any> {
    val basicContainer: BasicContainer<E>
}

interface IContainer<E : Any> : BasicContainer<E>, HasBasicContainer<E> {
    fun forEach(consumer: (E) -> Unit)
}

class Container<E : Any>
private constructor(private val basicContainerImpl: BasicContainerImpl<E>) :
    HasBasicContainer<E>,
    BasicContainer<E> by basicContainerImpl,
    IContainer<E> {
    override val basicContainer: BasicContainer<E> = basicContainerImpl

    constructor() : this(BasicContainerImpl())

    override fun forEach(consumer: (E) -> Unit) = basicContainerImpl.list.forEach(consumer)
}

private class BasicContainerImpl<E : Any> : BasicContainer<E> {
    val list = mutableListOf<E>()

    override fun add(e: E) {
        list.add(e)
    }

    override fun remove(e: Any) {
        list.remove(e)
    }

    override operator fun plusAssign(e: E) {
        list.add(e)
    }

    override operator fun minusAssign(e: Any) {
        list.remove(e)
    }
}
