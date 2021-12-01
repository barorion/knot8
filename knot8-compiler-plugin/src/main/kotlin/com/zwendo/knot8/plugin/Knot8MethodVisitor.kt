package com.zwendo.knot8.plugin

import com.zwendo.knot8.plugin.assertion.NotZeroAssertion
import com.zwendo.knot8.plugin.assertion.PositivityAssertion
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class Knot8MethodVisitor(
    private val original: MethodVisitor,
    private val access: Int,
    private val name: String,
    private val desc: String,
    private val signature: String,
    private val exceptions: Array<out String>,
    private val parameters: List<FunctionParameter>
) : MethodVisitor(API_VERSION, original) {
    val onVisitCode = mutableListOf<(MethodVisitor) -> Boolean>()

    override fun visitParameterAnnotation(parameter: Int, descriptor: String, visible: Boolean): AnnotationVisitor {
        val default: AnnotationVisitor = super.visitParameterAnnotation(parameter, descriptor, visible)
        return visitSpecificAnnotation(
            descriptor,
            AnnotationTarget.PARAMETER,
            default,
            parameter,
            visible,
            parameters[parameter]
        )
    }

    override fun visitCode() {
        // TODO add check for constructors
        val iterator = onVisitCode.iterator()
        while (iterator.hasNext()) {
            if (iterator.next()(original)) {
                iterator.remove()
            }
        }
        super.visitCode()
    }

    private fun visitSpecificAnnotation(
        descriptor: String,
        target: AnnotationTarget,
        default: AnnotationVisitor,
        paramIndex: Int,
        visible: Boolean,
        parameter: FunctionParameter,
    ): AnnotationVisitor {
        val func = nameToAnnotationVisitor[descriptor] ?: return default
        return func(target, this, default, paramIndex, visible, parameter)
    }

    companion object {
        private val nameToAnnotationVisitor: Map<String, AnnotationVisitorFunction> = hashMapOf(
            Pair(NotZeroAssertion.NOT_ZERO_INTERNAL_NAME) { target, knot8Visitor, aVisitor, index, visible, parameter ->
                NotZeroAssertion(
                    target,
                    knot8Visitor,
                    aVisitor,
                    index,
                    visible,
                    parameter,
                )
            },
            Pair(PositivityAssertion.POS_OR_ZERO_INTERNAL_NAME) { target, knot8Visitor, aVisitor, index, visible, parameter ->
                PositivityAssertion(
                    target,
                    knot8Visitor,
                    aVisitor,
                    index,
                    visible,
                    parameter,
                )
            },
        )
    }
}

private typealias AnnotationVisitorFunction = (
    target: AnnotationTarget,
    knot8methodVisitor: Knot8MethodVisitor,
    default: AnnotationVisitor,
    paramIndex: Int,
    visible: Boolean,
    parameter: FunctionParameter
) -> AnnotationVisitor
