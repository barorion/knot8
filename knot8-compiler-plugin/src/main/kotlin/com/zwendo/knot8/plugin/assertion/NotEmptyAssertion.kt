package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.*
import com.zwendo.knot8.plugin.AnnotationTarget
import com.zwendo.knot8.plugin.util.ANNOTATIONS_INTERNAL_NAME
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes

internal class NotEmptyAssertion(
    data: Knot8AnnotationVisitorData
) : AbstractAssertionAnnotation(
    data,
    NAME,
    "${data.parameter.name} $ERROR_MESSAGE",
    TARGETS
) {

    init {
        val type = data.parameter.type
        // asserts that target has a method named isEmpty (string, map or colleciton)
        if (type != TypeAdapters.STRING && type.findFirstSuperInterface(VALID_SUPERTYPES) == null) {
                throw Knot8IllegalAnnotationTargetTypeException(paramFqName, NAME, type.canonicalName)

        }
    }

    companion object {
        private const val NAME = "NotEmpty"
        const val DESCRIPTOR = "L$ANNOTATIONS_INTERNAL_NAME$NAME;"
        private val VALID_SUPERTYPES = setOf(
            Collection::class.java,
            Map::class.java,
        )
        private const val ERROR_MESSAGE = "is marked as non-empty but is empty."
        val TARGETS = setOf(AnnotationTarget.PARAMETER)
    }

    override fun writeAssertionTest(visitor: MethodVisitor): Label = with(visitor) {
        visitVarInsn(Opcodes.ALOAD, data.parameter.index)
        invokeIsEmpty()
        val label = Label()
        visitJumpInsn(Opcodes.IFEQ, label)
        return label
    }

    private fun MethodVisitor.invokeIsEmpty() {
        val type = data.parameter.type
        if (type == TypeAdapters.STRING) {
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, type.internalName, "isEmpty", "()Z", false)
        } else {
            visitMethodInsn(Opcodes.INVOKEINTERFACE, type.internalName, "isEmpty", "()Z", true)
        }
    }
}
