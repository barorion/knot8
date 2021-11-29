package com.zwendo.knot8.plugin.adapter

import com.zwendo.knot8.plugin.*
import com.zwendo.knot8.plugin.AnnotationTarget
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class NotZeroAnnotationVisitor(
    private val target: AnnotationTarget,
    private val default: MethodVisitor,
    private val knot8MethodVisitor: Knot8MethodVisitor
) :
    AnnotationVisitor(API_VERSION) {
    init {
        assertValidTarget(target)
    }

    companion object {
        internal const val NOT_ZERO = "L" + BYTECODE_ANNOTATIONS_PATH + "NotZero;"

        fun throwAttributeError(invalidAttributeName: String?): Nothing {
            throw Knot8IllegalAnnotationAttributeException("NotZero", invalidAttributeName ?: "")
        }
    }

    private fun writeAssertion() {
        TODO("Implement writeAssertionMethod")
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?): Nothing = throwAttributeError(name)

    override fun visitAnnotation(name: String?, descriptor: String?): Nothing = throwAttributeError(name)

    override fun visitArray(name: String?): Nothing = throwAttributeError(name)

    override fun visit(name: String?, value: Any?): Nothing = throwAttributeError(name)

    override fun visitEnd() {
        knot8MethodVisitor.onVisitCode += { writeAssertion() }
        super.visitEnd()
    }

    private fun assertValidTarget(target: AnnotationTarget): Unit = when (target) {
        AnnotationTarget.PARAMETER,
        AnnotationTarget.METHOD -> Unit
        else -> throw Knot8IllegalAnnotationTargetException(target)
    }
}
