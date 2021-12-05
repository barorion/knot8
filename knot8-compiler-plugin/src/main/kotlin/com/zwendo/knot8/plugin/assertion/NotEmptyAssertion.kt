package com.zwendo.knot8.plugin.assertion

import com.zwendo.knot8.plugin.AnnotationTarget
import com.zwendo.knot8.plugin.AnnotationVisitorData
import org.jetbrains.org.objectweb.asm.Label
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class NotEmptyAssertion(
    data: AnnotationVisitorData
) : AbstractAssertionAnnotation(data, NOT_EMPTY_NAME, "", TARGETS) {

    private companion object {
        const val NOT_EMPTY_NAME = "NotEmpty"
        val TARGETS = setOf(AnnotationTarget.PARAMETER)
    }

    override fun writeAssertionTest(visitor: MethodVisitor): Label {
        TODO("Not yet implemented")
    }
}
