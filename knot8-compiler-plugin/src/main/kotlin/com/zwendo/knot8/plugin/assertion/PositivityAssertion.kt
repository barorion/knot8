package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.ANNOTATIONS_INTERNAL_NAME
import com.zwendo.knot8.plugin.AnnotationTarget
import com.zwendo.knot8.plugin.FunctionParameter
import com.zwendo.knot8.plugin.Knot8MethodVisitor
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

internal class PositivityAssertion(
    target: AnnotationTarget,
    knot8MethodVisitor: Knot8MethodVisitor,
    default: AnnotationVisitor,
    index: Int,
    private val visible: Boolean, // TODO mind to use this
    private val parameter: FunctionParameter,
) : NumberAssertion("PosOrZero", target, knot8MethodVisitor, default, index) {

    companion object {
        const val POS_OR_ZERO_INTERNAL_NAME = "L${ANNOTATIONS_INTERNAL_NAME}PosOrZero;"
    }

    override fun writeAssertion(visitor: MethodVisitor): Boolean {
        basicAssertion(
            visitor,
            parameter.type,
            Opcodes.IFGE,
            "${parameter.name} is marked positive-or-zero but is negative."
        )
        return true
    }
}
