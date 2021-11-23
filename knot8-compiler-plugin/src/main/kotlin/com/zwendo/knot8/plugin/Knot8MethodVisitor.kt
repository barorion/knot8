package com.zwendo.knot8.plugin

import com.zwendo.knot8.plugin.adapter.annotation.NOT_ZERO
import org.jetbrains.org.objectweb.asm.AnnotationVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor

internal class Knot8MethodVisitor(original: MethodVisitor) : MethodVisitor(API_VERSION, original) {

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor {
        return findAnnotationVisitor(descriptor) { super.visitAnnotation(descriptor, visible) }
    }

}


private fun findAnnotationVisitor(descriptor: String, default: () -> AnnotationVisitor): AnnotationVisitor =
    when (descriptor) {
        NOT_ZERO -> default()
        else -> default()
    }

