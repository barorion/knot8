package com.zwendo.knot8.plugin.knot8visitor

import com.zwendo.knot8.plugin.AnnotationTarget
import com.zwendo.knot8.plugin.util.FunctionParameter
import org.jetbrains.org.objectweb.asm.AnnotationVisitor

/**
 * Represents the data required to create a Knot8 annotation visitor.
 *
 * @constructor creates a [Knot8AnnotationVisitorData] instance.
 * @property target the target of the annotation
 * @property knot8MethodVisitor the [Knot8MethodVisitor] associated to the annotation
 * @property default the default annotation visitor
 * @property isVisibleAtRuntime true if the annotation is visible at runtime; false otherwise
 */
internal data class Knot8AnnotationVisitorData(
    val target: AnnotationTarget,
    val knot8MethodVisitor: Knot8MethodVisitor,
    val default: AnnotationVisitor,
    val isVisibleAtRuntime: Boolean,
    val parameter: FunctionParameter,
)
