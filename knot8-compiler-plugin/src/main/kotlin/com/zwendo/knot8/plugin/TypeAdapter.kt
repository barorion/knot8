package com.zwendo.knot8.plugin

import org.jetbrains.org.objectweb.asm.Opcodes

internal class TypeAdapter private constructor(
    val typeClass: Class<out Any>,
    val internalName: String,
) {
    val fqName: String = typeClass.canonicalName
    val descriptor: String = if (typeClass.isPrimitive || typeClass.isArray) internalName else "L$internalName;"
    val isIntEquivalent: Boolean = when (typeClass) {
        Int::class.java,
        Byte::class.java,
        Short::class.java,
        Char::class.java -> true
        else -> false
    }
    val zeroConstOpCode: Int? = when (typeClass) {
        Int::class.java,
        Byte::class.java,
        Short::class.java,
        Char::class.java -> Opcodes.ICONST_0
        Float::class.java -> Opcodes.FCONST_0
        Double::class.java -> Opcodes.DCONST_0
        Long::class.java -> Opcodes.LCONST_0
        else -> null
    }
    val cmpOpCode: Int? = when (typeClass) {
        Float::class.java -> Opcodes.FCMPL
        Double::class.java -> Opcodes.DCMPL
        Long::class.java -> Opcodes.LCMP
        else -> null
    }

    private constructor(clazz: Class<out Any>) : this(clazz, clazz.name.replace(".", "/"))

    companion object {
        private val BOOLEAN = TypeAdapter(Boolean::class.java, "Z")
        private val CHAR = TypeAdapter(Char::class.java, "C")
        private val BYTE = TypeAdapter(Byte::class.java, "B")
        private val SHORT = TypeAdapter(Short::class.java, "S")
        private val INT = TypeAdapter(Int::class.java, "I")
        private val FLOAT = TypeAdapter(Float::class.java, "F")
        private val LONG = TypeAdapter(Long::class.java, "J")
        private val DOUBLE = TypeAdapter(Double::class.java, "D")
        private val VOID = TypeAdapter(Void.TYPE, "V")
        private val PRIMITIVES = listOf(BOOLEAN, CHAR, BYTE, SHORT, INT, FLOAT, LONG, DOUBLE, VOID)
        private val ANY_TO_PRIM = PRIMITIVES.flatMap {
            listOf(
                it.fqName to it,
                it.internalName to it,
                it.typeClass to it
            )
        }.toMap<Any, TypeAdapter>()

        fun fromClass(clazz: Class<out Any>): TypeAdapter = ANY_TO_PRIM[clazz] ?: TypeAdapter(clazz)

        fun fromString(type: String): TypeAdapter {
            val typeAdapter = ANY_TO_PRIM[type]
            if (typeAdapter != null) {
                return typeAdapter
            }
            try {
                val clazz = Class.forName(type.anyToFqName(), false, TypeAdapter::class.java.classLoader)
                return TypeAdapter(clazz)
            } catch (_: ClassNotFoundException) {
                throw IllegalArgumentException("Invalid type provided, no class found for $type.")
            }
        }

        private fun String.anyToFqName(): String {
            val desc = if (startsWith("L") && endsWith(";")) {
                substring(1, length - 1)
            } else {
                this
            }
            return desc.replace("/", ".")
        }
    }

    fun doesInheritsFrom(clazz: Class<out Any>): Boolean = typeClass.doesInheritsFrom(clazz)

    fun findFirstSuperInterface(interfaces: Collection<Class<out Any>>): Class<out Any>? {
        return typeClass.findFirstSuperInterface(interfaces)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as TypeAdapter

        if (typeClass != other.typeClass) return false

        return true
    }

    override fun hashCode(): Int {
        return typeClass.hashCode()
    }

    override fun toString(): String {
        return "TypeAdapter(${typeClass.canonicalName})"
    }
}

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
