package com.zwendo.knot8.plugin

import org.jetbrains.org.objectweb.asm.Opcodes
import org.jetbrains.org.objectweb.asm.Type


private val primitiveTypes = setOf("Z", "C", "B", "S", "I", "F", "J", "D")

internal fun Type.isPrimitive(): Boolean = primitiveTypes.contains(internalName)

internal fun Type.getConstZeroOpCode(): Int = when (sort) {
    Type.INT -> Opcodes.ICONST_0
    Type.FLOAT -> Opcodes.FCONST_0
    Type.DOUBLE -> Opcodes.DCONST_0
    Type.LONG -> Opcodes.LCONST_0
    else -> throw IllegalArgumentException("Unsupported type: $internalName")
}

internal fun Type.getCmpOpCode(): Int = when (sort) {
    Type.FLOAT -> Opcodes.FCMPL
    Type.DOUBLE -> Opcodes.DCMPL
    Type.LONG -> Opcodes.LCMP
    else -> throw IllegalArgumentException("Unsupported type: $internalName")
}

internal fun Type.isIntOrEquivalent(): Boolean = when (sort) {
    Type.INT,
    Type.BYTE,
    Type.SHORT,
    Type.CHAR -> true
    else -> false
}
