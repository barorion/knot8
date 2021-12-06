package com.zwendo.knot8.plugin.knot8visitor

import com.zwendo.knot8.plugin.AnnotationTarget
import com.zwendo.knot8.plugin.MethodKind
import com.zwendo.knot8.plugin.assertion.NotEmptyAssertion
import com.zwendo.knot8.plugin.assertion.NumberAssertion
import com.zwendo.knot8.plugin.util.FunctionParameter
import com.zwendo.knot8.plugin.util.ProjectConstants
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

/**
 * The Knot8 custom [MethodVisitor]. An instance will parse a method code and modify it according to the encountered
 * annotations.
 */
internal class Knot8MethodVisitor(
    private val data: Knot8MethodVisitorData,
    private val parameters: List<FunctionParameter>,
) : MethodVisitor(ProjectConstants.API_VERSION, data.original) {
    var onMethodEnter = mutableListOf<(MethodVisitor) -> Unit>()
        private set
    private var isInitialized = false
    val methodCanonicalName: String = "${data.classType.canonicalName}#${data.methodName}"

    //region method visitor overrides
    override fun visitParameterAnnotation(parameter: Int, descriptor: String, visible: Boolean): AnnotationVisitor {
        val default: AnnotationVisitor = super.visitParameterAnnotation(parameter, descriptor, visible)
        // adds 1 when the is 'this' in stack
        val paramIndex = if (data.kind == MethodKind.STATIC) parameter else parameter + 1
        return visitSpecificAnnotation(
            descriptor,
            AnnotationTarget.PARAMETER,
            default,
            visible,
            parameters[paramIndex]
        )
    }

    override fun visitCode() {
        // waits for super constructor call
        super.visitCode()
        if (data.kind != MethodKind.CONSTRUCTOR) {
            onMethodEnter()
        }
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
        if ((data.kind == MethodKind.CONSTRUCTOR) and !isInitialized) {
            onMethodEnter()
            isInitialized = true
        }
    }
    //endregion

    //region private methods
    /**
     * The method called just before starting writing method code. It notifies all observers registered and then
     * unregister them.
     */
    private fun onMethodEnter() {
        onMethodEnter.forEach { it(data.original) }
        onMethodEnter = mutableListOf()
    }

    private fun visitSpecificAnnotation(
        descriptor: String,
        target: AnnotationTarget,
        default: AnnotationVisitor,
        visible: Boolean,
        parameter: FunctionParameter,
    ): AnnotationVisitor {
        val annotationProvider = nameToAnnotationVisitor[descriptor] ?: return default
        val data = Knot8AnnotationVisitorData(target, this, default, visible, parameter)
        return annotationProvider(data)
    }
    //endregion

    companion object {
        private val nameToAnnotationVisitor: Map<String, (Knot8AnnotationVisitorData) -> AnnotationVisitor> = hashMapOf(
            NumberAssertion.NOT_ZERO_DESCRIPTOR to { NumberAssertion.notZero(it) },
            NumberAssertion.POSITIVE_OR_ZERO_DESCRIPTOR to { NumberAssertion.positiveOrZero(it) },
            NumberAssertion.POSITIVE_DESCRIPTOR to { NumberAssertion.positive(it) },
            NotEmptyAssertion.DESCRIPTOR to { NotEmptyAssertion(it) },
        )
    }
}
