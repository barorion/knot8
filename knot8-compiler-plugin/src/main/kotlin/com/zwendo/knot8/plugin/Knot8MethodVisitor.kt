package com.zwendo.knot8.plugin

import com.zwendo.knot8.plugin.adapter.NotZeroAnnotationVisitor.Companion.NOT_ZERO
import com.zwendo.knot8.plugin.adapter.NotZeroAnnotationVisitor
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class Knot8MethodVisitor(
    private val original: MethodVisitor,
    private val methodType: Knot8ClassBuilder.MethodType
) :
    MethodVisitor(API_VERSION, original) {

    val onVisitCode = mutableListOf<() -> Unit>()

    override fun visitParameterAnnotation(parameter: Int, descriptor: String, visible: Boolean): AnnotationVisitor {
        visitSpecificAnnotation(descriptor, AnnotationTarget.PARAMETER, original, visible)
        return super.visitParameterAnnotation(parameter, descriptor, visible)
    }

    override fun visitCode() {
        onVisitCode.forEach { it() }
        super.visitCode()
    }

    override fun visitEnd() {
        // TODO visit stacked instructions
        super.visitEnd()
    }

    private fun visitSpecificAnnotation(
        descriptor: String,
        target: AnnotationTarget,
        visitor: MethodVisitor,
        visible: Boolean
    ): Unit {
        when (descriptor) {
            NOT_ZERO -> NotZeroAnnotationVisitor(target, visitor, this);
        }
    }
}
