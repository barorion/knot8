package com.zwendo.knot8.plugin.util

import org.jetbrains.org.objectweb.asm.Opcodes

/**
 * Represents a JVM type in all its forms (Class, full qualified name, internal name and descriptor).
 */
internal class TypeAdapter private constructor(
    val typeClass: Class<out Any>,
    val internalName: String,
) {
    // different typeClass names
    val fqName: String = typeClass.canonicalName
    val descriptor: String = if (typeClass.isPrimitive || typeClass.isArray) internalName else "L$internalName;"

    // typeClass delegation
    val isPrimitive: Boolean = typeClass.isPrimitive
    val isArray: Boolean = typeClass.isArray
    val canonicalName: String = typeClass.canonicalName

    // typeClass specs
    val isIntEquivalent: Boolean = when (typeClass) {
        Int::class.java,
        Byte::class.java,
        Short::class.java,
        Char::class.java -> true
        else -> false
    }
    val loadOpCode: Int = when (typeClass) {
        Int::class.java,
        Byte::class.java,
        Short::class.java,
        Char::class.java -> Opcodes.ILOAD
        Float::class.java -> Opcodes.FLOAD
        Double::class.java -> Opcodes.DLOAD
        Long::class.java -> Opcodes.LLOAD
        else -> Opcodes.ALOAD
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
        private val TYPE_PREFIXES = setOf("Z", "C", "B", "S", "I", "F", "J", "D", "V", "L")

        /**
         * Creates a [TypeAdapter] from a java [Class].
         *
         * @param clazz the class to create the adapter
         * @return the created [TypeAdapter]
         */
        fun fromClass(clazz: Class<out Any>): TypeAdapter = ANY_TO_PRIM[clazz] ?: TypeAdapter(clazz)

        /**
         * Creates a [TypeAdapter] from a [String] in the form of a full qualified name,
         * a type descriptor or an internal name.
         *
         * @param string the string to create the adapter
         * @return the created [TypeAdapter]
         * @throws IllegalArgumentException if the given string does not represent a type.
         */
        fun fromString(string: String): TypeAdapter {
            val typeAdapter = ANY_TO_PRIM[string]
            if (typeAdapter != null) {
                return typeAdapter
            }
            try {
                val clazz = Class.forName(string.anyToFqName(), false, TypeAdapter::class.java.classLoader)
                return TypeAdapter(clazz)
            } catch (_: ClassNotFoundException) {
                throw IllegalArgumentException("Invalid type provided, no class found for $string.")
            }
        }

        /**
         * Creates the list of [TypeAdapter] of parameters from a method descriptor.
         *
         * @param descriptor the descriptor of the method to create the adapter
         * @return the created [TypeAdapter]
         * @throws IllegalArgumentException if the given string does not represent a method descriptor
         */
        fun fromMethodDescriptor(descriptor: String): List<TypeAdapter> {
            if (!(descriptor.startsWith('(') && descriptor.contains(')'))) {
                throw IllegalArgumentException("Invalid method descriptor: $descriptor")
            }
            val list = mutableListOf<TypeAdapter>()
            var dimensions = 0
            var i = 1
            while (i < descriptor.length) {
                val ch = descriptor[i]
                when {
                    ch == ')' -> break // end of args
                    ch == '[' -> dimensions++ // array encountered
                    ch != 'V' && TYPE_PREFIXES.contains(ch.toString()) -> { // valid type prefix
                        val typeName = "[".repeat(dimensions) + if (descriptor[i] == 'L') {
                            descriptor.substring(i, descriptor.indexOf(';', i) + 1) // if object type
                        } else { // if primitive
                            descriptor[i].toString()
                        }
                        list += fromString(typeName)
                        i += typeName.length - dimensions // shift index
                        dimensions = 0 // reset dimension
                        continue
                    }
                    else -> throw IllegalArgumentException("Invalid method descriptor: $descriptor")
                }
                i++
            }
            return list
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

    fun cmpOpCode(): Int = when (typeClass) {
        Float::class.java -> Opcodes.FCMPL
        Double::class.java -> Opcodes.DCMPL
        Long::class.java -> Opcodes.LCMP
        else -> throw UnsupportedOperationException("Unsupported method for type: $canonicalName")
    }

    fun zeroConstOpCode(): Int = when (typeClass) {
        Int::class.java,
        Byte::class.java,
        Short::class.java,
        Char::class.java -> Opcodes.ICONST_0
        Float::class.java -> Opcodes.FCONST_0
        Double::class.java -> Opcodes.DCONST_0
        Long::class.java -> Opcodes.LCONST_0
        else -> throw UnsupportedOperationException("Unsupported method for type: $canonicalName")
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
