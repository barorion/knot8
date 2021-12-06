package com.zwendo.knot8.plugin.util

import org.jetbrains.org.objectweb.asm.Opcodes

internal object ProjectConstants {
    // Project ASM api version
    const val API_VERSION: Int = Opcodes.ASM9

    // Project group id
    const val GROUP_INTERNAL_NAME: String = "com/zwendo/"

    // Project root package
    const val PROJECT_INTERNAL_NAME: String = "knot8/"

    // Project annotations package internal name
    const val ANNOTATIONS_INTERNAL_NAME: String = "$GROUP_INTERNAL_NAME${PROJECT_INTERNAL_NAME}annotation/"
}

