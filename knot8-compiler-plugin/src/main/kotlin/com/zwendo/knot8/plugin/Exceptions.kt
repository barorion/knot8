package com.zwendo.knot8.plugin

sealed class Knot8Exception(message: String): Exception(message)

class Knot8IllegalAnnotationParameter(message: String): Knot8Exception(message)
