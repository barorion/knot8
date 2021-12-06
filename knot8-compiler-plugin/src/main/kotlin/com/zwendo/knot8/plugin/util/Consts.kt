package com.zwendo.knot8.plugin.util

import org.jetbrains.org.objectweb.asm.Opcodes

// Project ASM api version
internal const val API_VERSION: Int = Opcodes.ASM9

// Project group id
internal const val GROUP_INTERNAL_NAME: String = "com/zwendo/"

// Project root package
internal const val PROJECT_INTERNAL_NAME: String = "knot8/"

// Project annotations package internal name
internal const val ANNOTATIONS_INTERNAL_NAME: String = "$GROUP_INTERNAL_NAME${PROJECT_INTERNAL_NAME}annotation/"
