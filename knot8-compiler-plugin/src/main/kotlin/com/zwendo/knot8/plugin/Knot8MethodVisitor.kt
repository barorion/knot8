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
    val onMethodEnter = mutableListOf<(MethodVisitor) -> Unit>()
    private var isInitialized = false
    private val methodKind: MethodKind = MethodKind.getKind(name, access)

    override fun visitParameterAnnotation(parameter: Int, descriptor: String, visible: Boolean): AnnotationVisitor {
        val default: AnnotationVisitor = super.visitParameterAnnotation(parameter, descriptor, visible)
        // adds 1 when the is 'this' in stack
        val paramIndex = if (methodKind == MethodKind.STATIC) parameter else parameter + 1
        return visitSpecificAnnotation(
            descriptor,
            AnnotationTarget.PARAMETER,
            default,
            parameter,
            visible,
            parameters[paramIndex]
        )
    }

    override fun visitCode() {
        // waits for super constructor call
        if (methodKind != MethodKind.CONSTRUCTOR) {
            onMethodEnter()
        }
        super.visitCode()
    }

    override fun visitMethodInsn(
        opcode: Int,
        owner: String?,
        name: String?,
        descriptor: String?,
        isInterface: Boolean
    ) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface)
        // notify observers right after super constructor call
        if ((methodKind == MethodKind.CONSTRUCTOR) and !isInitialized) {
            onMethodEnter()
            isInitialized = true
        }
    }

    private fun onMethodEnter() = onMethodEnter.forEach { it(original) }

    private fun visitSpecificAnnotation(
        descriptor: String,
        target: AnnotationTarget,
        default: AnnotationVisitor,
        paramIndex: Int,
        visible: Boolean,
        parameter: FunctionParameter,
    ): AnnotationVisitor {
        val annotationProvider = nameToAnnotationVisitor[descriptor] ?: return default
        val data = AnnotationVisitorData(target, this, default, visible, parameter, access)
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
    val isMethodVisible: Boolean,
    val parameter: FunctionParameter,
    val methodAccess: Int,
)

private typealias AnnotationVisitorFunction = (AnnotationVisitorData) -> AnnotationVisitor
