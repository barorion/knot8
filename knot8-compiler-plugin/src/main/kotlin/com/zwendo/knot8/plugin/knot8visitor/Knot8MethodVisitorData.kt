package com.zwendo.knot8.plugin.knot8visitor

import com.zwendo.knot8.plugin.MethodKind
import com.zwendo.knot8.plugin.util.TypeAdapter
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal data class Knot8MethodVisitorData(
    val classType: TypeAdapter,
    val original: MethodVisitor,
    val methodAccess: Int,
    val methodName: String,
    val methodDesc: String,
    val methodSignature: String?,
    val methodExceptions: Array<out String>?,
    val kind: MethodKind,
)
