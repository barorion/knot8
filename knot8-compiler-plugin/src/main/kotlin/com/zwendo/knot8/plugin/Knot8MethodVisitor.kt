package com.zwendo.knot8.plugin

import com.zwendo.knot8.plugin.assertion.NumberAssertion
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class Knot8MethodVisitor(
    private val original: MethodVisitor,
    private val access: Int,
    private val name: String,
    private val desc: String,
    private val signature: String,
    private val exceptions: Array<out String>,
    private val parameters: List<FunctionParameter>,
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
        val annotationProvider = nameToAnnotationVisitor[descriptor] ?: return default
        val data = AnnotationVisitorData(target, this, default, paramIndex, visible, parameter, access)
        return annotationProvider(data)
    }

    companion object {
        private val nameToAnnotationVisitor: Map<String, AnnotationVisitorFunction> = hashMapOf(
            NumberAssertion.NOT_ZERO_NAME to { NumberAssertion.notZero(it) },
            NumberAssertion.POSITIVE_OR_ZERO_NAME to { NumberAssertion.positiveOrZero(it) },
            NumberAssertion.POSITIVE_NAME to { NumberAssertion.positive(it) },
        )
    }
}

internal data class AnnotationVisitorData(
    val target: AnnotationTarget,
    val knot8MethodVisitor: Knot8MethodVisitor,
    val default: AnnotationVisitor,
    val paramIndex: Int,
    val visible: Boolean,
    val parameter: FunctionParameter,
    val access: Int,
)

private typealias AnnotationVisitorFunction = (AnnotationVisitorData) -> AnnotationVisitor
