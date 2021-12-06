package com.zwendo.knot8.plugin.util

/**
 * Represents a function parameter, an instance of this class contains all information about a parameter in a function.
 *
 * @constructor creates a [FunctionParameter] instance
 * @property name the name of the parameter
 * @property type the type of the parameter
 * @property index the index of the parameter in the local variables stack.
 */
internal data class FunctionParameter(val name: String, val type: TypeAdapter, val index: Int)