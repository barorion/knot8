package com.zwendo.knot8.plugin

import java.util.Locale

/**
 * Represents an annotation target.
 */
enum class AnnotationTarget {
    FIELD,
    METHOD,
    PARAMETER;

    override fun toString(): String = super.toString().lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}