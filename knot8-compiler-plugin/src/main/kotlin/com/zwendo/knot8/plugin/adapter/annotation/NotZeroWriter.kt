package com.zwendo.knot8.plugin.adapter.annotation

import com.zwendo.knot8.plugin.Consts.API_VERSION
import org.jetbrains.org.objectweb.asm.AnnotationVisitor

internal const val NOT_ZERO = "Lcom/zwendo/knot8/annotation/NotZero;"

internal class NotZeroWriter : AnnotationVisitor(API_VERSION) {

    override fun visit(name: String?, value: Any?) {
        super.visit(name, value)
    }

    override fun visitEnum(name: String?, descriptor: String?, value: String?) {

    }

    override fun visitAnnotation(name: String?, descriptor: String?): AnnotationVisitor {
        return super.visitAnnotation(name, descriptor)
    }

    override fun visitArray(name: String?): AnnotationVisitor {
        return super.visitArray(name)
    }

    override fun visitEnd() {
        super.visitEnd()
    }
}