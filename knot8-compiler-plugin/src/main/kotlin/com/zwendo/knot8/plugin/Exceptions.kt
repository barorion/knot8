package com.zwendo.knot8.plugin

/**
 * Represents the Knot8 plugin base exception.
 *
 * @constructor Creates a Knot8ExceptionInstance.
 *
 * @param message the detail message
 */
sealed class Knot8Exception(message: String) : Exception(message)

/**
 * Knot8 exception thrown when an annotation has illegal attributes. Probably due to the presence of an
 * annotation with the same classpath as an annotation presents in Knot8 annotations package.
 *
 * @constructor Creates a Knot8IllegalAnnotationAttributeException instance.
 *
 * @param annotationName the name of the annotation involved in this exception
 * @param invalidAttributeName the name of the illegal attribute involved in this exception
 */
class Knot8IllegalAnnotationAttributeException internal constructor(
    annotationName: String,
    invalidAttributeName: String
) :
    Knot8Exception("$annotationName annotation has no attribute named: $invalidAttributeName")

/**
 * Knot8 exception thrown when an annotation is used on an illegal target. Probably due to the presence of an
 * annotation with the same classpath as an annotation presents in Knot8 annotations package.
 *
 * @constructor Creates a Knot8IllegalAnnotationTargetException instance.
 *
 * @param target the illegal target involved in this exception
 */
class Knot8IllegalAnnotationTargetException internal constructor(target: Target) :
    Knot8Exception("illegal annotation target: $target")

