package com.zwendo.knot8.plugin

/**
 * Represents the Knot8 plugin base exception.
 *
 * @constructor Creates a [Knot8Exception] instance.
 *
 * @param message the detail message
 */
sealed class Knot8Exception(message: String) : Exception(message)

/**
 * Knot8 exception thrown when an annotation has illegal an attribute. Probably due to the presence of an
 * annotation with the same classpath as an annotation presents in Knot8 annotations package.
 *
 * @constructor Creates a [Knot8IllegalAnnotationAttributeException] instance.
 *
 * @param annotationName the name of the annotation involved in this exception
 * @param invalidAttributeName the name of the illegal attribute involved in this exception
 */
class Knot8IllegalAnnotationAttributeException internal constructor(
    annotationName: String,
    invalidAttributeName: String
) : Knot8Exception("$annotationName annotation has no attribute named: $invalidAttributeName")

/**
 * Knot8 exception thrown when an annotation is used on an illegal target. Probably due to the presence of an
 * annotation with the same classpath as an annotation presents in Knot8 annotations package.
 *
 * @constructor Creates a [Knot8IllegalAnnotationTargetException] instance.
 *
 * @param annotationName the name of the annotation involved in this exception
 * @param target the illegal target involved in this exception
 */
class Knot8IllegalAnnotationTargetException internal constructor(annotationName: String, target: AnnotationTarget) :
    Knot8Exception("Illegal annotation target, annotation $annotationName doesn't support $target.")

/**
 * Knot8 exception thrown when an annotation is used on a target (variable, field or parameter) which the type is incompatible
 * with the annotation used on this target
 *
 * @constructor Creates a [Knot8IllegalAnnotationTargetTypeException] instance.
 *
 * @param annotationName the name of the annotation involved in this exception
 * @param targetTypeInternalName the internal type of the annotation target
 */
class Knot8IllegalAnnotationTargetTypeException internal constructor(annotationName: String, targetTypeInternalName: String) :
    Knot8Exception(
        "Illegal annotation target type, annotation $annotationName doesn't support ${
            targetTypeInternalName.internalToFqName()
        } type.")