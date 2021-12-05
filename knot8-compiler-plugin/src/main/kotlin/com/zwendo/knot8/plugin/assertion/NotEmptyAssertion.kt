package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.*
import com.zwendo.knot8.plugin.AnnotationTarget
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor
import org.jetbrains.org.objectweb.asm.Opcodes
import java.util.*

internal class NotEmptyAssertion(
    data: Knot8AnnotationVisitorData
) : AbstractAssertionAnnotation(data, NAME, "${data.parameter.name} $ERROR_MESSAGE", TARGETS) {
    private val owner: String = run {
        val type = data.parameter.type
        val typeFqName = type.internalName.internalToFqName()
        if (type.internalName == STRING_INTERNAL_NAME) {
            return@run STRING_INTERNAL_NAME
        } else {
            if (data.parameter.type.isPrimitive()) {
                throw Knot8IllegalAnnotationTargetTypeException(NAME, typeFqName)
            }
            val typeClass = Class.forName(typeFqName, false, javaClass.classLoader)
            val matchingType = typeClass.findFirstInterface(VALID_SUPERTYPES)
            if (matchingType != null) {
                //typeClass.declaredMethods.contains(Method.)
                return@run type.internalName.fqNameToInternal()
            }
            throw Knot8IllegalAnnotationTargetTypeException(NAME, typeFqName)
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
