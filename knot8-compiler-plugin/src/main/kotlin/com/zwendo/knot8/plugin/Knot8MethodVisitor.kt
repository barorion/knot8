package com.zwendo.knot8.plugin

import com.zwendo.knot8.plugin.assertion.NumberAssertion
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

/**
 * The Knot8 custom [MethodVisitor]. An instance will parse a method code and modify it according to the encountered
 * annotations.
 */
internal class Knot8MethodVisitor(
    private val original: MethodVisitor,
    private val access: Int,
    private val name: String,
    private val desc: String,
    private val signature: String,
    private val exceptions: Array<out String>,
    private val parameters: List<FunctionParameter>,
) : MethodVisitor(API_VERSION, original) {
    var onMethodEnter = mutableListOf<(MethodVisitor) -> Unit>()
        private set
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
            visible,
            parameters[paramIndex]
        )
    }

    override fun visitCode() {
        // waits for super constructor call
        super.visitCode()
        if (methodKind != MethodKind.CONSTRUCTOR) {
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
        if ((methodKind == MethodKind.CONSTRUCTOR) and !isInitialized) {
            onMethodEnter()
            isInitialized = true
        }
    }

    /**
     * The method called just before starting writing method code. It notifies all observers registered and then
     * unregister them.
     */
    private fun onMethodEnter() {
        onMethodEnter.forEach { it(original) }
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
        val data = Knot8AnnotationVisitorData(target, this, default, visible, parameter, access)
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

/**
 * Represents the data required to create a Knot8 annotation visitor.
 *
 * @constructor creates a [Knot8AnnotationVisitorData] instance.
 * @param target the target of the annotation
 * @param knot8MethodVisitor the [Knot8MethodVisitor] associated to the annotation
 * @param default the default annotation visitor
 * @param isVisibleAtRuntime true if the annotation is visible at runtime; false otherwise
 * @param methodAccess the flags concerning the method access
 */
internal data class Knot8AnnotationVisitorData(
    val target: AnnotationTarget,
    val knot8MethodVisitor: Knot8MethodVisitor,
    val default: AnnotationVisitor,
    val isVisibleAtRuntime: Boolean,
    val parameter: FunctionParameter,
    val methodAccess: Int,
)

private typealias AnnotationVisitorFunction = (Knot8AnnotationVisitorData) -> AnnotationVisitor
