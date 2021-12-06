package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.AnnotationTarget
import com.zwendo.knot8.plugin.knot8visitor.Knot8AnnotationVisitorData
import com.zwendo.knot8.plugin.Knot8IllegalAnnotationAttributeException
import com.zwendo.knot8.plugin.Knot8IllegalAnnotationTargetException
import com.zwendo.knot8.plugin.util.ProjectConstants
import org.jetbrains.org.objectweb.asm.*

internal abstract class AbstractAssertionAnnotation(
    protected val data: Knot8AnnotationVisitorData,
    private val annotationName: String,
    private val exceptionMessage: String,
    targets: Set<AnnotationTarget>,
) : AnnotationVisitor(ProjectConstants.API_VERSION) {
    protected val paramFqName: String = "${data.knot8MethodVisitor.methodCanonicalName}.${data.parameter.name}"

    init {
        if (!targets.contains(data.target)) { // asserts that target is valid
            throw Knot8IllegalAnnotationTargetException(paramFqName, annotationName, data.target)
        }
    }

    //region private methods
    private fun throwAttributeError(invalidAttributeName: String): Nothing {
        throw Knot8IllegalAnnotationAttributeException(paramFqName, annotationName, invalidAttributeName)
    }

    /**
     * Method that must be overwritten by subclasses and which is registered to method visitor to be
     * called when the visitCode method is called.
     *
     * @param visitor the visitor to use to modify bytecode
     * @return true if the method should be unregistered after it has been called ; false otherwise
     */
    private fun writeAssertion(visitor: MethodVisitor): Unit = with (visitor) {
        val valueIsOk = writeAssertionTest(visitor)
        val iaeInternalName: String = Type.getInternalName(IllegalArgumentException::class.java)
        visitTypeInsn(Opcodes.NEW, iaeInternalName)
        visitInsn(Opcodes.DUP)
        visitLdcInsn(exceptionMessage)
        visitMethodInsn(Opcodes.INVOKESPECIAL, iaeInternalName, "<init>", "(Ljava/lang/String;)V", false)
        visitInsn(Opcodes.ATHROW)
        visitLabel(valueIsOk)
    }
    //endregion

    protected abstract fun writeAssertionTest(visitor: MethodVisitor): Label

    //region annotation visitor default overrides
    override fun visitEnum(name: String, descriptor: String, value: String): Nothing = throwAttributeError(name)

    override fun visitAnnotation(name: String, descriptor: String): Nothing = throwAttributeError(name)

    override fun visitArray(name: String): Nothing = throwAttributeError(name)

    override fun visit(name: String, value: Any): Nothing = throwAttributeError(name)
    //endregion

    override fun visitEnd() {
        data.knot8MethodVisitor.onMethodEnter.add(this::writeAssertion) // registers assertion writing
        data.default.visitEnd()
    }
}
