package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.*
import com.zwendo.knot8.plugin.AnnotationTarget
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
    private val owner: String = run {
        val typeInternalName: String = data.parameter.type.internalName
        val typeFqName: String = data.parameter.type.internalName.internalToFqName()

        if (typeInternalName == STRING_INTERNAL_NAME) { // if target is a string
            return@run STRING_INTERNAL_NAME
        } else {
            if (data.parameter.type.isPrimitive()) { // check first if type is not a primitive
                throw Knot8IllegalAnnotationTargetTypeException(paramFqName, NAME, typeFqName)
            }
            val typeClass = Class.forName(typeFqName, false, javaClass.classLoader) // load type class
            val matchingType = typeClass.findFirstSuperInterface(VALID_SUPERTYPES) // find first matching interface
            if (matchingType != null) {
                return@run typeInternalName
            }
            throw Knot8IllegalAnnotationTargetTypeException(paramFqName, NAME, typeFqName)
        }
    }

    companion object {
        private const val NAME = "NotEmpty"
        val DESCRIPTOR = "$ANNOTATIONS_INTERNAL_NAME$NAME".internalToDescriptor()

        private const val STRING_INTERNAL_NAME = "java/lang/String"
        private val VALID_SUPERTYPES = setOf(
            Collection::class.java,
            Map::class.java,
            java.util.Collection::class.java,
            java.util.Map::class.java,
        )
        private const val ERROR_MESSAGE = "is marked as non-empty but is empty."
        val TARGETS = setOf(AnnotationTarget.PARAMETER)
        val map = mapOf<Any, Any>().isEmpty()

    }

    override fun writeAssertionTest(visitor: MethodVisitor): Label = with(visitor) {
        visitVarInsn(Opcodes.ALOAD, data.parameter.index)
        invokeIsEmpty()
        val label = Label()
        visitJumpInsn(Opcodes.IFEQ, label)
        return label
    }

    private fun MethodVisitor.invokeIsEmpty() {
        if (owner == STRING_INTERNAL_NAME) {
            visitMethodInsn(Opcodes.INVOKEVIRTUAL, owner, "isEmpty", "()Z", false)
        } else {
            visitMethodInsn(Opcodes.INVOKEINTERFACE, owner, "isEmpty", "()Z", true)
        }
    }
}
