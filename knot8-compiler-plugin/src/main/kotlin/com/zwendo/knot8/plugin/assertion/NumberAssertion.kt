package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.*
import com.zwendo.knot8.plugin.AnnotationTarget
import org.jetbrains.org.objectweb.asm.*

internal abstract class NumberAssertion(
    private val annotationName: String,
    protected val target: AnnotationTarget,
    protected val knot8MethodVisitor: Knot8MethodVisitor,
    protected val default: AnnotationVisitor,
    protected val index: Int,
) : AnnotationVisitor(API_VERSION) {

    init {
        if (!TARGETS.contains(target)) { // asserts that target is valid
            throw Knot8IllegalAnnotationTargetException(annotationName, target)
        }
    }

    private companion object {
        val TARGETS = setOf(AnnotationTarget.PARAMETER)
    }

    private fun throwAttributeError(invalidAttributeName: String?): Nothing {
        throw Knot8IllegalAnnotationAttributeException(annotationName, invalidAttributeName ?: "")
    }

    /**
     * Method that must be overwritten by subclasses and which is registered to method visitor to be
     * called when the visitCode method is called.
     *
     * @param visitor the visitor to use to modify bytecode
     * @return true if the method should be unregistered after it has been called ; false otherwise
     */
    protected abstract fun writeAssertion(visitor: MethodVisitor): Boolean

    protected fun basicAssertion(visitor: MethodVisitor, type: Type, compOp: Int, message: String) = with(visitor) {
        if (!type.isPrimitive()) { // asserts that type is primitive
            throw Knot8IllegalAnnotationTargetTypeException(annotationName, type.internalName)
        }
        visitVarInsn(type.getOpcode(Opcodes.ILOAD), index) // load parameter
        if (!type.isIntOrEquivalent()) { // if necessary compare to type corresponding 0
            visitInsn(type.getConstZeroOpCode())
            visitInsn(type.getCmpOpCode())
        }
        val valueIsOk = Label()
        visitJumpInsn(compOp, valueIsOk)
        val iaeInternalName: String = Type.getInternalName(IllegalArgumentException::class.java)
        visitTypeInsn(Opcodes.NEW, iaeInternalName)
        visitInsn(Opcodes.DUP)
        visitLdcInsn(message)
        visitMethodInsn(Opcodes.INVOKESPECIAL, iaeInternalName, "<init>", "(Ljava/lang/String;)V", false)
        visitInsn(Opcodes.ATHROW)
        visitLabel(valueIsOk)
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?): Nothing = throwAttributeError(name)

    override fun visitAnnotation(name: String?, descriptor: String?): Nothing = throwAttributeError(name)

    override fun visitArray(name: String?): Nothing = throwAttributeError(name)

    override fun visit(name: String?, value: Any?): Nothing = throwAttributeError(name)

    override fun visitEnd() {
        knot8MethodVisitor.onVisitCode.add(this::writeAssertion) // registers assertion writing
        default.visitEnd()
    }
}
