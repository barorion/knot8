package com.zwendo.knot8.plugin

import java.util.Locale

enum class Target {
    FIELD,
    METHOD,
    PARAMETER,
    CONSTRUCTOR;

    override fun toString(): String = super.toString().lowercase().replaceFirstChar {
        if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
    }
}