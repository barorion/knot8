package com.zwendo.knot8.plugin.util

/**
 * Class containing [TypeAdapter] constants representing the most common types.
 */
internal object TypeAdapters {
    val BOOLEAN = TypeAdapter.fromClass(Boolean::class.java)
    val CHAR = TypeAdapter.fromClass(Char::class.java)
    val BYTE = TypeAdapter.fromClass(Byte::class.java)
    val SHORT = TypeAdapter.fromClass(Short::class.java)
    val INT = TypeAdapter.fromClass(Int::class.java)
    val FLOAT = TypeAdapter.fromClass(Float::class.java)
    val LONG = TypeAdapter.fromClass(Long::class.java)
    val DOUBLE = TypeAdapter.fromClass(Double::class.java)
    val VOID = TypeAdapter.fromClass(Void.TYPE)

    val UNIT = TypeAdapter.fromClass(Unit::class.java)
    val COLLECTION = TypeAdapter.fromClass(Collection::class.java)
    val MAP = TypeAdapter.fromClass(Map::class.java)
    val STRING = TypeAdapter.fromClass(String::class.java)
}