package com.zwendo.knot8.plugin

import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type


private val primitiveTypes = hashMapOf(
    "Z" to "boolean",
    "C" to "char",
    "B" to "byte",
    "S" to "short",
    "I" to "int",
    "F" to "float",
    "J" to "long",
    "D" to "double"
)

/**
 * Tests if a [this] is a primitive type.
 *
 * @return true if [this] is a primitive type; false otherwise
 */
internal fun Type.isPrimitive(): Boolean = primitiveTypes.contains(internalName)

/**
 * Gets the zero corresponding to the type of [this].
 *
 * @return the zero corresponding to [this]
 */
internal fun Type.getConstZeroOpCode(): Int = when (sort) {
    Type.INT -> Opcodes.ICONST_0
    Type.FLOAT -> Opcodes.FCONST_0
    Type.DOUBLE -> Opcodes.DCONST_0
    Type.LONG -> Opcodes.LCONST_0
    else -> throw IllegalArgumentException("Unsupported type: $internalName")
}

/**
 * Gets the comparison [Opcodes] for non int primitives.
 *
 * @return the comparison [Opcodes] corresponding to [this]
 */
internal fun Type.getCmpOpCode(): Int = when (sort) {
    Type.FLOAT -> Opcodes.FCMPL
    Type.DOUBLE -> Opcodes.DCMPL
    Type.LONG -> Opcodes.LCMP
    else -> throw IllegalArgumentException("Unsupported type: $internalName")
}

/**
 * Tests if [this] type can use int zero const for its operations.
 *
 * @return true if [this] type can use int zero; false otherwise
 */
internal fun Type.canUseIntZero(): Boolean = when (sort) {
    Type.INT,
    Type.BYTE,
    Type.SHORT,
    Type.CHAR -> true
    else -> false
}

/**
 * Tests if an [this] which is representing binary flags has particular flags
 *
 * @param flags the different flags, separated by a [or] or a [plus]
 * @return true if [this] has all the flags; else otherwise
 */
private fun Int.hasFlags(flags: Int): Boolean = this and flags == flags

/**
 * Tests if an [this] which is representing binary flags has particular flags
 *
 * @param flags the different flags, separated by a comma
 * @return true if [this] has all the flags; else otherwise
 */
internal fun Int.hasFlags(vararg flags: Int): Boolean = hasFlags(flags.sum())

/**
 * Converts [this] (a class internal name) to a fq name.
 *
 * @return the fq name corresponding to [this]
 */
internal fun String.internalToFqName() = primitiveTypes.getOrDefault(this, this.replace("/", "."))

/**
 * Converts [this] (a class fq name) to a descriptor.
 *
 * @return the descriptor corresponding to [this]
 */
internal fun String.fqNameToDescriptor() = "L${this.replace(".", "/")};"

/**
 * Converts [this] (a class internal name) to a descriptor.
 *
 * @return the descriptor corresponding to [this]
 */
internal fun String.internalToDescriptor() = "L$this;"

internal fun String.fqNameToInternal() = this.replace(".", "/")

internal fun Class<out Any>.doesInheritsFrom(clazz: Class<out Any>): Boolean {
    return (this == clazz) || (superclass?.doesInheritsFrom(clazz) ?: false)
}

internal fun Class<out Any>.findFirstSuperInterface(interfaces: Collection<Class<out Any>>): Class<out Any>? {
    return if (interfaces.contains(this)) {
        this
    } else {
        this.interfaces.find { it.findFirstSuperInterface(interfaces) != null }
    }
}
