package com.zwendo.knot8.plugin.util

/**
 * Class containing [TypeAdapter] constants representing the most common types.
 */
internal object TypeAdapters {
    //region Primitives
    /**
     * [TypeAdapter] for primitive boolean.
     */
    val BOOLEAN = TypeAdapter.fromClass(Boolean::class.java)
    /**
     * [TypeAdapter] for primitive char.
     */
    val CHAR = TypeAdapter.fromClass(Char::class.java)
    /**
     * [TypeAdapter] for primitive byte.
     */
    val BYTE = TypeAdapter.fromClass(Byte::class.java)
    /**
     * [TypeAdapter] for primitive short.
     */
    val SHORT = TypeAdapter.fromClass(Short::class.java)
    /**
     * [TypeAdapter] for primitive int.
     */
    val INT = TypeAdapter.fromClass(Int::class.java)
    /**
     * [TypeAdapter] for primitive float.
     */
    val FLOAT = TypeAdapter.fromClass(Float::class.java)
    /**
     * [TypeAdapter] for primitive long.
     */
    val LONG = TypeAdapter.fromClass(Long::class.java)
    /**
     * [TypeAdapter] for primitive double.
     */
    val DOUBLE = TypeAdapter.fromClass(Double::class.java)
    /**
     * [TypeAdapter] for primitive void type.
     */
    val VOID = TypeAdapter.fromClass(Void.TYPE)
    //endregion

    //region Most commons object types
    /**
     * [TypeAdapter] for Kotlin [Unit] type.
     */
    val UNIT = TypeAdapter.fromClass(Unit::class.java)
    /**
     * [TypeAdapter] for [Collection] and [java.util.Collection] types.
     */
    val COLLECTION = TypeAdapter.fromClass(Collection::class.java)
    /**
     * [TypeAdapter] for [MAP] and [java.util.Map] types.
     */
    val MAP = TypeAdapter.fromClass(Map::class.java)
    /**
     * [TypeAdapter] for [String] and [java.lang.String] types.
     */
    val STRING = TypeAdapter.fromClass(String::class.java)
    //endregion
}