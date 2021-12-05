package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.*
import com.zwendo.knot8.plugin.AnnotationTarget
import org.jetbrains.org.objectweb.asm.*

internal class NumberAssertion private constructor(
    data: Knot8AnnotationVisitorData,
    private val assertion: Type
) : AbstractAssertionAnnotation(
    data,
    assertion.annotationName,
    "${data.parameter.name} ${assertion.errorMessage}",
    TARGETS
) {

    /**
     * Represents the different number assertions.
     *
     * @param opcode the opcode to use for testing the parameter
     * @param annotationName the name of the annotation
     * @param rule the rule that the tested parameter has to follow
     * @param illegalStateName the name of the illegal state of the tested parameter
     */
    private enum class Type(
        val opcode: Int,
        val annotationName: String,
        rule: String,
        illegalStateName: String
    ) {
        NOT_ZERO(Opcodes.IFNE, "NotZero", "non-zero", "zero"),
        POSITIVE_OR_ZERO(Opcodes.IFGE, "PosOrZero", "positive or zero", "negative"),
        POSITIVE(Opcodes.IFGT, "Positive", "positive", "negative or zero");

        val errorMessage = "is marked as $rule but is $illegalStateName."
        val descriptor = "${ANNOTATIONS_INTERNAL_NAME}$annotationName".internalToDescriptor()
    }

    init {
        val typeName = data.parameter.type.internalName
        if (!VALID_TYPES.contains(typeName)) {
            throw Knot8IllegalAnnotationTargetTypeException(assertion.annotationName, typeName.internalToFqName())
        }
    }

    companion object {
        private val TARGETS = setOf(AnnotationTarget.PARAMETER)
        private val VALID_TYPES = setOf("B", "S", "I", "F", "J", "D")

        val NOT_ZERO_DESCRIPTOR = Type.NOT_ZERO.descriptor
        val POSITIVE_OR_ZERO_DESCRIPTOR = Type.POSITIVE_OR_ZERO.descriptor
        val POSITIVE_DESCRIPTOR = Type.POSITIVE.descriptor

        fun notZero(data: Knot8AnnotationVisitorData) = NumberAssertion(data, Type.NOT_ZERO)

        fun positiveOrZero(data: Knot8AnnotationVisitorData) = NumberAssertion(data, Type.POSITIVE_OR_ZERO)

        fun positive(data: Knot8AnnotationVisitorData) = NumberAssertion(data, Type.POSITIVE)
    }

    override fun writeAssertionTest(visitor: MethodVisitor): Label = with(visitor) {
        val type = data.parameter.type
        visitVarInsn(type.getOpcode(Opcodes.ILOAD), data.parameter.index) // load parameter
        if (!type.canUseIntZero()) { // if necessary compare to type corresponding 0
            visitInsn(type.getConstZeroOpCode())
            visitInsn(type.getCmpOpCode())
        }
        val label = Label()
        visitJumpInsn(assertion.opcode, label)
        return label
    }
}
